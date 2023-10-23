package neoAtlantis.utils.apps.parameters.objects;

import java.util.ArrayList;
import java.util.List;
import neoAtlantis.utils.apps.parameters.exceptions.MemoryParameterLoaderException;
import neoAtlantis.utils.apps.parameters.interfaces.MemoryParametersLoader;
import org.apache.log4j.Logger;

/**
 * Objeto que representa una colección de parametros en memoria
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryParameters {
    private static final Logger DEBUGGER = Logger.getLogger(MemoryParameters.class);
    
    private ArrayList<Parameter> parameters=new ArrayList();
    private MemoryParametersLoader loader;
    
    /**
     * Constructor
     * @param loader Cargador de parametros a utilizar
     */
    public MemoryParameters(MemoryParametersLoader loader){
        this.loader=loader;
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * Recupera el numero de parametros en memoria
     * @return 
     */
    public int size(){
        return this.parameters.size();
    }

    /**
     * Recupera el valor de un parametro en memoria
     * @param param Parametro del que se desea el valor
     * @return 
     */
    public String getParameterValue(String param){
        for(Parameter p: this.parameters){
            if( p.getParameter().equals(param) ){
                return p.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * Recupera un parametro en memoria
     * @param param Parametro que se desea
     * @return 
     */
    public Parameter getParameter(String param){
        for(Parameter p: this.parameters){
            if( p.getParameter().equals(param) ){
                return p;
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene la lista de parametros en memoria
     * @return Lista con los nombres de los parametros
     */
    public List<String> getParametersKeys(){
        ArrayList<String> keys=new ArrayList<String>();
        
        for(Parameter p: this.parameters){
            keys.add(p.getParameter());
        }
        
        return keys;
    }
    
    /**
     * Agrega un parametro a la lista
     * @param param Parametro a agregar
     * @param value Valor del parametro
     * @param description Descripción del parametro
     * @param type Tipo de parametro 
     */
    public void addParameter(String param, String value, String description, String type){
        ParameterType tipo=ParameterType.parse(type);
        
        if(type==null){
            tipo=ParameterType.STRING;
        }

        addParameter(param, value, description, tipo);
    }
    
    /**
     * Agrega un parametro a la lista
     * @param param Parametro a agregar
     * @param value Valor del parametro
     * @param description Descripción del parametro
     * @param type Tipo de parametro 
     */
    public void addParameter(String param, String value, String description, ParameterType type){
        boolean existe=false;
        
        for(int i=0; i<this.parameters.size(); i++){
            if( this.parameters.get(i).getParameter().equals(param) ){
                this.parameters.get(i).setValue(value);
                existe=true;
                break;
            }
        }        
        
        //si no existe
        if( !existe ){
            Parameter p=new Parameter();
            p.setParameter(param);
            p.setDescription(description);
            p.setValue(value);
            p.setType(type);
            this.parameters.add(p);
        }
    }
    
    /**
     * Actualiza un parametro en memoria
     * @param parameter Parametro a actualizar
     * @param value Valor del parametro
     * @return true su lo logro actualizar
     * @throws neoAtlantis.utils.apps.parameters.exceptions.MemoryParameterLoaderException
     */
    public boolean updateParameter(String parameter, String value) throws MemoryParameterLoaderException{
        if( this.loader.updateParameter(parameter, value) ){
            DEBUGGER.debug("Se actualizo el parametro '"+parameter+"', vuelvo a cargar toda la informacion");
            
            this.loader.loadParameters(this);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        
        sb.append("||--------------------------------  MemoryParameters  --------------------------------||\n");
        sb.append("Numero: ").append(this.parameters.size()).append("\n");
        for(Parameter p: this.parameters){
            sb.append(p.getParameter()).append("(").append(p.getType()).append("=").append(p.getValue()).append("     ['").append(p.getDescription()).append("']\n");
        }
        sb.append("||------------------------------------------------------------------------------------||\n");
        
        return sb.toString();
    }
}
