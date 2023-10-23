package neoAtlantis.utils.apps.catalogs.objetcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryCatalogs {
    static final Logger LOGGER = Logger.getLogger(MemoryCatalogs.class);
    
    private List<MemoryTable> catalogos=new ArrayList<MemoryTable>();
//    private CatalogsLoader loader;
    
/*    public MemoryCatalogs(CatalogsLoader loader){
        this.loader=loader;
    }*/
    
    public int size(){
        return this.catalogos.size();
    }

    public int getCatalogsInMemory(){
        int i=0;
        
        for(MemoryTable t: this.catalogos){
            if( t.isInMemory() ){
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
            if( cTmp.getName().equalsIgnoreCase(tab.getName()) ){
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
            if( t.getName().equalsIgnoreCase(cat) && t.isInMemory() ){                                
                for(int i=0; i<t.getData().size(); i++){
                    dTmp=new String[2];
                    agrega=true;
                    
                    for(int j=0; j<t.getColumns().size(); j++){
                        if( !t.getColumns().get(j).isVisible() ){
                            continue;
                        }
                        if( t.getColumns().get(j).isActivity() && !((Boolean)t.getData().get(i)[j]) ){
                            agrega=false;
                            break;
                        }
                            
                        if( t.getColumns().get(j).isKey() ){
                            if( dTmp[0]==null ){
                                dTmp[0]="";
                            }
                            else{
//                                dTmp[0]+=CatalogsLoader.SEPARADOR_LLAVE;
                            }
                            
                            dTmp[0]+=t.getData().get(i)[j];
                        }
                        else{
                            if( dTmp[1]==null ){
                                dTmp[1]="";
                            }
                            else{
                                dTmp[1]+=" ";
                            }
                            
                            dTmp[1]+=t.getData().get(i)[j];
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
            if(t.getName().equals(cat) && t.isInMemory()){
                d=t.getData();

                break;
            }
            //si no esta en memoria
            if(t.getName().equals(cat) && !t.isInMemory()){
                try{
//                    d=this.loader.getData(t);
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
            if( t.isInMemory() ){
                int[] posKey;
                String kTmp="";
                HashMap<String,Object> d=new HashMap<String,Object>();
                
                LOGGER.debug("Recupera los datos de memoria.");
                posKey=new int[t.getKeys().size()];
                //descubro las llaves
                for(int j=0,i=0; j<t.getColumns().size(); j++){
                    if( t.getColumns().get(j).isKey() ){
                        posKey[i]=j;
                        i++;
                    }
                }

                //obtengo los datos
                for(int i=0; i<t.getData().size(); i++){
                    kTmp="";
                    for(int j=0; j<posKey.length; j++){
                        if( !kTmp.isEmpty() ){
//                            kTmp+=CatalogsLoader.SEPARADOR_LLAVE;
                        }

                        kTmp+=t.getData().get(i)[posKey[j]];
                    }

                    //valida si es la misma llave
                    if( kTmp.equals(key) ){
                        LOGGER.debug("Localiza los datos para la llave: "+kTmp);
                        for(int j=0; j<t.getData().get(i).length; j++){
                            d.put(t.getColumns().get(j).getName(), t.getData().get(i)[j]);
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
//                    return this.loader.getDataById(t, key);
                }
                catch(Exception ex){
                    LOGGER.error("No se lograron obtener los datos de la tabla '"+t.getName()+"' con llave '"+key+"'", ex);
                }
            }
        }
        
        return null;
    }
    
    public long getRecordsCount(String cat){
        long r=0;
        
        for(MemoryTable t: this.catalogos){
            //si esta en memoria
            if(t.getName().equals(cat) && t.isInMemory()){
                r=t.getSizeRecords();

                break;
            }
            //si no esta en memoria
            if(t.getName().equals(cat) && !t.isInMemory()){
                try{
//                    t.setRegs(this.loader.getRecordsCount(t));
                    r=t.getSizeRecords();
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
            if( t.getName().equalsIgnoreCase(catalogo) ){
                return t;
            }
        }
        
        return null;
    }

    public int updateData(MemoryTable t, Map<String,Object> data) throws Exception{
/*        int i=this.loader.updateData(t, data);
        
        if( i>0 ){
            t.asignaDatos( this.loader.getData(t) );
        }
        
        return i;*/return 0;
    }
    
    public int addData(MemoryTable t, Map<String,Object> data) throws Exception{
        /*int i=this.loader.addData(t, data);

        if( i>0 ){
            t.asignaDatos( this.loader.getData(t) );
        }
        
        return i;*/return 0;
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
