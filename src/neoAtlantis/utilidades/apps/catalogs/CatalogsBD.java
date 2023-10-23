package neoAtlantis.utilidades.apps.catalogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import neoAtlantis.utilidades.apps.catalogs.interfaces.CatalogsLoader;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryColumn;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryTable;
import neoAtlantis.utilidades.apps.catalogs.objects.TypeData;
import neoAtlantis.utilidades.bd.ConfigurationDB;
import org.apache.log4j.Logger;


/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class CatalogsBD implements CatalogsLoader {
    static final Logger logger = Logger.getLogger(CatalogsBD.class);

    protected StringBuffer sql;
    protected Properties config;

    /**
     * Genera un CatalogsLoader por Base de Datos.
     * @param xml Flujo del archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public CatalogsBD(InputStream xml) throws Exception {
        this.config=ConfigurationDB.parseConfiguracionXML(xml);
    }

    /**
     * Genera un CatalogsLoader por Base de Datos.
     * @param xml Archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public CatalogsBD(File xml) throws Exception {
        this(new FileInputStream(xml));
    }

    /**
     * Genera un CatalogsLoader por Base de Datos.
     * @param xml Ruta completa del archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public CatalogsBD(String xml) throws Exception {
        this(new File(xml));
    }

    /**
     * Genera un CatalogsLoader por Base de Datos, tomando los valores por default para los campos en la BD.
     * @param configBD Configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public CatalogsBD(Properties configBD) throws Exception {
        ConfigurationDB.validaConfigProperties(configBD);
        this.config=configBD;
    }

    public CatalogsBD(String driver, String url, String user, String pass) throws Exception {
        this.config.setProperty("driver", driver);
        this.config.setProperty("url", url);
        this.config.setProperty("user", user);
        this.config.setProperty("pass", pass);
        ConfigurationDB.validaConfigProperties(this.config);
    }

    //---------------------------------------------------------------------------------

    public MemoryCatalogs loadCatalogs(MemoryCatalogs mc) throws Exception {
        MemoryColumn cTmp;
        
        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            DatabaseMetaData meta=con.getMetaData();
            ResultSet res2, res=meta.getTables(null, null, null, new String[]{"TABLE"});
            logger.debug("Obtengo información de la BD.");

            //recupera informacion de tablas
            while(res.next()){  
                for(MemoryTable tTmp: mc.getCatalogs()){
                    if( tTmp.getNombre().equalsIgnoreCase(res.getString(3)) ){
                        logger.debug("Carga configuración del catalogo '"+tTmp.getNombre()+"'.");
                        res2=meta.getColumns(null, null, tTmp.getNombre(), null);

                        //recupera informacion de campos
                        while(res2.next()){
                            cTmp=new MemoryColumn(res2.getString(4));
                            switch( res2.getInt(5) ){
                                case Types.BIGINT:
                                case Types.INTEGER:
                                case Types.ROWID:
                                case Types.SMALLINT:
                                case Types.TINYINT:{
                                    cTmp.setTipo(TypeData.ENTERO);
                                    break;
                                }
                                case Types.DECIMAL:
                                case Types.DOUBLE:
                                case Types.FLOAT:
                                case Types.NUMERIC:
                                case Types.REAL:{
                                    cTmp.setTipo(TypeData.DECIMAL);
                                    break;
                                }
                                case Types.BIT:
                                case Types.BOOLEAN:{
                                    cTmp.setTipo(TypeData.BOLEANO);
                                    break;
                                }
                                case Types.DATE:
                                case Types.TIME:
                                case Types.TIMESTAMP:{
                                    cTmp.setTipo(TypeData.FECHA);
                                    break;
                                }
                                default:{
                                    cTmp.setTipo(TypeData.CARACTER);
                                    break;
                                }
                            }
                            cTmp.setTamano(res2.getInt(7));

                            tTmp.agregaColumna(cTmp);                             
                        }
                        res2.close();

                        //recupera informacion de llaves
                        if( tTmp.getLlaves().isEmpty() ){
                            res2=meta.getPrimaryKeys(null, null, tTmp.getNombre());
                            while(res2.next()){
                                for(MemoryColumn c: tTmp.getColumnas()){
                                    if( c.getNombre().equalsIgnoreCase(res2.getString(4)) ){
                                        tTmp.agregaLlave(c.getNombre());
                                        break;
                                    }
                                }
                            }
                        }

                        if( tTmp.isEnMemoria() ){
                            //recupera datos del catalogo
                            tTmp.asignaDatos( this.getData(tTmp) );
                        }
                    }
                }
            }

            res.close();
            con.close();

            //limpia datos no validos
            this.removeDataInvalid(mc);
        }catch(Exception ex){
            logger.fatal("No se logro recuperar los parametros.", ex);
            throw ex;
        }
        
        return mc;
    }

    public List<Object[]> getData(MemoryTable t){
        ArrayList<Object[]> d=new ArrayList<Object[]>();
        
        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            Statement st=con.createStatement();
            ResultSet res=st.executeQuery(this.getQuerySelect(t));                    
            Object[] dato;
            
            while(res.next()){
                dato=new Object[t.getColumnas().size()];

                for(int i=0; i<t.getColumnas().size(); i++){
                    switch( t.getColumnas().get(i).getTipo() ){
                        case ENTERO:{
                            dato[i]=res.getInt(i+1);
                            break;
                        }
                        case DECIMAL:{
                            dato[i]=res.getDouble(i+1);
                            break;
                        }
                        case BOLEANO:{
                            dato[i]=res.getBoolean(i+1);
                            break;
                        }
                        case FECHA:{
                            dato[i]=res.getDate(i+1);
                            break;
                        }
                        default:{
                            dato[i]=res.getString(i+1);
                        }
                    }                                                
                }
                
                d.add(dato);
            }
            
            res.close();
            st.close();
            con.close();
        }
        catch(Exception ex){
            logger.error("No se logro recuperar los datos de: "+t.getNombre(), ex);
        }
        
        return d;
    }

    public long getRecordsCount(MemoryTable t) throws Exception {
        long num=0;
        
        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            Statement st=con.createStatement();
            ResultSet res=st.executeQuery(getQueryCount(t));                    
            
            if(res.next()){
                num=res.getLong(1);
            }
            
            res.close();
            st.close();
            con.close();
        }
        catch(Exception ex){
            logger.error("No se logro recuperar la cantidad datos de: "+t.getNombre(), ex);
        }        
        
        return num;
    }

    public int updateData(MemoryTable t, Map<String, Object> data) throws Exception {
        int i=1;
        
        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            PreparedStatement st=con.prepareStatement(getQueryUpdate(t));
            
            for(MemoryColumn c: t.getColumnas()){
                if( !c.isCapturable()  ){
                    continue;
                }
                
                switch(c.getTipo()){
                    case ENTERO:{
                        st.setInt(i, (Integer)data.get(c.getNombre()));
                        break;
                    }
                    case DECIMAL:{
                        st.setDouble(i, (Double)data.get(c.getNombre()));
                        break;
                    }
                    case BOLEANO:{
                        st.setBoolean(i, (Boolean)data.get(c.getNombre()));
                        break;
                    }
                    case FECHA:{
                        //st.setDate(i, (Date)data.get(c.getNombre()));
                        break;
                    }
                    default:{
                        st.setString(i, (String)data.get(c.getNombre()));
                        break;
                    }
                }
                
                i++;
            }

            //para llaves
            for(MemoryColumn c: t.getLlaves()){
                System.out.println("st:"+st+", data:"+data+", c:"+c);
                switch(c.getTipo()){
                    case ENTERO:{
                        st.setInt(i, (Integer)data.get(c.getNombre()));
                        break;
                    }
                    case DECIMAL:{
                        st.setDouble(i, (Double)data.get(c.getNombre()));
                        break;
                    }
                    case BOLEANO:{
                        st.setBoolean(i, (Boolean)data.get(c.getNombre()));
                        break;
                    }
                    case FECHA:{
                        //st.setDate(i, (Date)data.get(c.getNombre()));
                        break;
                    }
                    default:{
                        st.setString(i, (String)data.get(c.getNombre()));
                        break;
                    }
                }
                
                i++;
            }
            
            logger.debug("Intenta actualizar el registro con datos: "+data);
            i=st.executeUpdate();
            
            st.close();
            con.close();
        }
        catch(Exception ex){
            logger.error("No se logro atualizar los datos de: "+t.getNombre(), ex);
            throw ex;
        }
        
        return i;
    }

    public int addData(MemoryTable t, Map<String, Object> data) throws Exception {
        int i=1;
        
        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            PreparedStatement st=con.prepareStatement(getQueryInsert(t));
            
            for(MemoryColumn c: t.getColumnas()){
                if( !c.isCapturable()  ){
                    continue;
                }
                
                switch(c.getTipo()){
                    case ENTERO:{
                        st.setInt(i, (Integer)data.get(c.getNombre()));//(String)ks[i-1]));
                        break;
                    }
                    case DECIMAL:{
                        st.setDouble(i, (Double)data.get(c.getNombre()));//(String)ks[i-1]));
                        break;
                    }
                    case BOLEANO:{
                        st.setBoolean(i, (Boolean)data.get(c.getNombre()));//(String)ks[i-1]));
                        break;
                    }
                    case FECHA:{
                        //st.setDate(i, (Date)data.get(ks[i-1]));
                        break;
                    }
                    default:{
                        st.setString(i, (String)data.get(c.getNombre()));//(String)ks[i-1]));
                        break;
                    }
                }
                
                i++;
            }
            
            logger.debug("Intenta agregar el registro con datos: "+data);
            i=st.executeUpdate();
            
            st.close();
            con.close();
        }
        catch(Exception ex){
            logger.error("No se logro agregar los datos a "+t.getNombre(), ex);
            throw ex;
        }
                
        return i;
    }

    public Map<String, Object> getDataById(MemoryTable t, String key) throws Exception {
        int[] posKey;
        Object[] dTmp;
        HashMap<String,Object> d=new HashMap<String,Object>();
        String kTmp="";

        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            Statement st=con.createStatement();
            ResultSet res=st.executeQuery(getQuerySelect(t));

            posKey=new int[t.getLlaves().size()];
            //descubro las llaves
            for(int j=0,i=0; j<t.getColumnas().size(); j++){
                if( t.getColumnas().get(j).isLlave() ){
                    posKey[i]=j;
                    i++;
                }
            }

            while( res.next() ){
                dTmp=new Object[t.getColumnas().size()];

                for(int j=0; j<t.getColumnas().size(); j++){
                    switch(t.getColumnas().get(j).getTipo()){
                        case ENTERO:{
                            dTmp[j]=res.getInt(j+1);
                            break;
                        }
                        case DECIMAL:{
                            dTmp[j]=res.getDouble(j+1);
                            break;
                        }
                        case FECHA:{
                            dTmp[j]=res.getDate(j+1);
                            break;
                        }
                        case BOLEANO:{
                            dTmp[j]=res.getBoolean(j+1);
                            break;
                        }
                        default:{
                            dTmp[j]=res.getString(j+1);
                        }
                    }
                }

                kTmp="";
                for(int i=0; i<posKey.length; i++){
                    if( !kTmp.isEmpty() ){
                        kTmp+=CatalogsLoader.SEPARADOR_LLAVE;
                    }

                    kTmp+=dTmp[posKey[i]];
                }

                //valida si es la misma llave
                if( kTmp.equals(key) ){
                    logger.debug("Localiza los datos para la llave: "+kTmp);
                    for(int i=0; i<t.getColumnas().size(); i++){
                        d.put(t.getColumnas().get(i).getNombre(), dTmp[i]);
                    }

                    break;
                }
            }

            res.close();
            st.close();
            con.close();
        }catch(Exception ex){
            logger.error("No se lograron recuperar los datos de: "+t.getNombre(), ex);
        }
        
        return d;
    }

    //---------------------------------------------------------------------------------
    
    private String getQuerySelect(MemoryTable tab){
        StringBuilder sb=new StringBuilder("");
        StringBuilder sb2=new StringBuilder("");
        
        sb.append("SELECT ");
        for(int i=0; i<tab.getColumnas().size(); i++){
            if( i>0 ){
                sb.append(", ");
            }
            sb.append(tab.getColumnas().get(i).getNombre());
        }
        sb.append(" FROM ").append(tab.getNombre()); 
        
        //para ordenar
        sb2=new StringBuilder("");
        for(int i=0; i<tab.getColumnas().size(); i++){
            if( tab.getColumnas().get(i).getOrdenacion()==null ){
                continue;
            }
            
            if( sb2.length()>0 ){
                sb2.append(", ");
            }
            sb2.append(tab.getColumnas().get(i).getNombre()).append(" ").append(tab.getColumnas().get(i).getOrdenacion());
        }
        
        if( sb2.length()>0 ){
            sb.append(" ORDER BY ").append(sb2);
        }
        
        logger.debug("Query generado: "+sb);
        
        return sb.toString();
    }

    private String getQueryCount(MemoryTable tab){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("SELECT COUNT(*) FROM ").append(tab.getNombre()); 
        
        return sb.toString();
    }
    
    private String getQueryUpdate(MemoryTable tabla){
        StringBuilder sb=new StringBuilder("");
        //StringBuilder sb2=new StringBuilder("");
        
        sb.append("UPDATE ").append(tabla.getNombre()).append(" SET ");
        for(int i=0,j=0; i<tabla.getColumnas().size(); i++){
            if( !tabla.getColumnas().get(i).isCapturable() ){
                continue;
            }
            
            if( j>0 ){
                sb.append(", ");
            }
            sb.append(tabla.getColumnas().get(i).getNombre()).append("=?");
            j++;
        }
        sb.append(" WHERE ");
        for(int i=0; i<tabla.getLlaves().size(); i++){
            if( i>0 ){
                sb.append(" AND ");
            }
            sb.append(tabla.getLlaves().get(i).getNombre()).append("=?");
        }
        
        logger.debug("Query generado: "+sb);
        
        return sb.toString();
    }

    private String getQueryInsert(MemoryTable tabla){
        StringBuilder sb=new StringBuilder("");
        StringBuilder sb2=new StringBuilder("");
        
        sb.append("INSERT INTO ").append(tabla.getNombre()).append(" (");
        for(int i=0; i<tabla.getColumnas().size(); i++){
            if( !tabla.getColumnas().get(i).isCapturable() ){
                continue;
            }
            
            if( sb2.length()>0 ){
                sb.append(", ");
                sb2.append(", ");
            }
            sb.append(tabla.getColumnas().get(i).getNombre());
            sb2.append("?");
        }
        sb.append(") VALUES (").append(sb2).append(");");
        
        logger.debug("Query generado: "+sb);
        
        return sb.toString();
    }

    private void removeDataInvalid(MemoryCatalogs mc){
        for(int i=mc.size()-1; i>=0; i--){
            if( mc.getCatalogs().get(i).getColumnas().isEmpty() ){
                mc.getCatalogs().remove(i);
                continue;
            }
                
            for(int j=mc.getCatalogs().get(i).getColumnas().size()-1; j>=0; j--){
                if( mc.getCatalogs().get(i).getColumnas().get(j).getTipo()==null ){
                    mc.getCatalogs().get(i).getColumnas().remove(j);
                }
            }
        }
    }

}
