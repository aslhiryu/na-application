package neoAtlantis.utilidades.apps.catalogs.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import neoAtlantis.utilidades.apps.catalogs.interfaces.CatalogsLoader;
import org.apache.log4j.Logger;


/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryCatalogs {
    static final Logger LOGGER = Logger.getLogger(MemoryCatalogs.class);
    
    private List<MemoryTable> catalogos=new ArrayList<MemoryTable>();
    private CatalogsLoader loader;
    
    public MemoryCatalogs(CatalogsLoader loader){
        this.loader=loader;
    }
    
    public int size(){
        return this.catalogos.size();
    }

    public int getCatalogsInMemory(){
        int i=0;
        
        for(MemoryTable t: this.catalogos){
            if( t.isEnMemoria() ){
                i++;
            }
        }
        
        return i;
    }
    
    public List<MemoryTable> getCatalogs(){
        return this.catalogos;
    }    

    public void addTable(MemoryTable tab){
        boolean existe=false;
        
        for(MemoryTable cTmp: this.catalogos){
            if( cTmp.getNombre().equalsIgnoreCase(tab.getNombre()) ){
                //iguala datos
                
                existe=true;
                break;
            }
        }
        
        if( !existe ){
            this.catalogos.add(tab);
        }
    }

    public List<String[]> getDataList(String cat){
        ArrayList <String[]> d=new ArrayList <String[]>();
        String[] dTmp;
        boolean agrega=true;
        
        for(MemoryTable t: this.catalogos){
            if( t.getNombre().equalsIgnoreCase(cat) && t.isEnMemoria() ){                                
                for(int i=0; i<t.getDatos().size(); i++){
                    dTmp=new String[2];
                    agrega=true;
                    
                    for(int j=0; j<t.getColumnas().size(); j++){
                        if( !t.getColumnas().get(j).isVisible() ){
                            continue;
                        }
                        if( t.getColumnas().get(j).isActividad() && !((Boolean)t.getDatos().get(i)[j]) ){
                            agrega=false;
                            break;
                        }
                            
                        if( t.getColumnas().get(j).isLlave() ){
                            if( dTmp[0]==null ){
                                dTmp[0]="";
                            }
                            else{
                                dTmp[0]+=CatalogsLoader.SEPARADOR_LLAVE;
                            }
                            
                            dTmp[0]+=t.getDatos().get(i)[j];
                        }
                        else{
                            if( dTmp[1]==null ){
                                dTmp[1]="";
                            }
                            else{
                                dTmp[1]+=" ";
                            }
                            
                            dTmp[1]+=t.getDatos().get(i)[j];
                        }
                    }
                    
                    if(agrega){
                        d.add(dTmp);
                    }
                }
            }
        }
        
        return d;
    }
    
    public List<Object[]> getData(String cat){
        List<Object[]> d=null;
        
        for(MemoryTable t: this.catalogos){
            //si esta en memoria
            if(t.getNombre().equals(cat) && t.isEnMemoria()){
                d=t.getDatos();

                break;
            }
            //si no esta en memoria
            if(t.getNombre().equals(cat) && !t.isEnMemoria()){
                try{
                    d=this.loader.getData(t);
                }
                catch(Exception ex){
                    LOGGER.error("No se logro recuperar la información de la tabla '"+cat+"' ", ex);
                }

                break;
            }

        }
            
        return d;
    }
    
    public Map<String,Object> getDataById(MemoryTable t, String key){
        LOGGER.debug("Busca los datos para la llave: "+key);
        
        if( t!=null ){
            //si existe en memoria
            if( t.isEnMemoria() ){
                int[] posKey;
                String kTmp="";
                HashMap<String,Object> d=new HashMap<String,Object>();
                
                LOGGER.debug("Recupera los datos de memoria.");
                posKey=new int[t.getLlaves().size()];
                //descubro las llaves
                for(int j=0,i=0; j<t.getColumnas().size(); j++){
                    if( t.getColumnas().get(j).isLlave() ){
                        posKey[i]=j;
                        i++;
                    }
                }

                //obtengo los datos
                for(int i=0; i<t.getDatos().size(); i++){
                    kTmp="";
                    for(int j=0; j<posKey.length; j++){
                        if( !kTmp.isEmpty() ){
                            kTmp+=CatalogsLoader.SEPARADOR_LLAVE;
                        }

                        kTmp+=t.getDatos().get(i)[posKey[j]];
                    }

                    //valida si es la misma llave
                    if( kTmp.equals(key) ){
                        LOGGER.debug("Localiza los datos para la llave: "+kTmp);
                        for(int j=0; j<t.getDatos().get(i).length; j++){
                            d.put(t.getColumnas().get(j).getNombre(), t.getDatos().get(i)[j]);
                        }

                        break;
                    }
                }
                
                return d;
            }
            //si no esta en memoria
            else{
                LOGGER.debug("Recupera los datos de BD.");
                try{
                    return this.loader.getDataById(t, key);
                }
                catch(Exception ex){
                    LOGGER.error("No se lograron obtener los datos de la tabla '"+t.getNombre()+"' con llave '"+key+"'", ex);
                }
            }
        }
        
        return null;
    }
    
    public long getRecordsCount(String cat){
        long r=0;
        
        for(MemoryTable t: this.catalogos){
            //si esta en memoria
            if(t.getNombre().equals(cat) && t.isEnMemoria()){
                r=t.getNumeroRegistros();

                break;
            }
            //si no esta en memoria
            if(t.getNombre().equals(cat) && !t.isEnMemoria()){
                try{
                    t.setRegs(this.loader.getRecordsCount(t));
                    r=t.getNumeroRegistros();
                }
                catch(Exception ex){
                    LOGGER.error("No se logro recuperar los registros de la tabla '"+cat+"' ", ex);
                }
                
                break;
            }
        }
        
        return r;
    }
    
    public MemoryTable getCatalog(String catalogo){
        for(MemoryTable t: this.catalogos){
            if( t.getNombre().equalsIgnoreCase(catalogo) ){
                return t;
            }
        }
        
        return null;
    }

    public int updateData(MemoryTable t, Map<String,Object> data) throws Exception{
        int i=this.loader.updateData(t, data);
        
        if( i>0 ){
            t.asignaDatos( this.loader.getData(t) );
        }
        
        return i;
    }
    
    public int addData(MemoryTable t, Map<String,Object> data) throws Exception{
        int i=this.loader.addData(t, data);

        if( i>0 ){
            t.asignaDatos( this.loader.getData(t) );
        }
        
        return i;
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("||--------------------------------  MemoryCatalogs  --------------------------------||\n");
        sb.append("Numero: ").append(this.catalogos.size()).append("\n");
        for(MemoryTable t: this.catalogos){
            sb.append(t).append("\n");
        }
        sb.append("||----------------------------------------------------------------------------------||\n");
        
        return sb.toString();
    }
    
}
