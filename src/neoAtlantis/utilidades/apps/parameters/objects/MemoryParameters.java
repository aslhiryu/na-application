package neoAtlantis.utilidades.apps.parameters.objects;

import java.util.ArrayList;
import java.util.List;
import neoAtlantis.utilidades.apps.parameters.interfaces.ParametersLoader;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryParameters {
    private ArrayList<MemoryParameter> params=new ArrayList<MemoryParameter>();
    private ParametersLoader loader;
    
    public MemoryParameters(ParametersLoader loader){
        this.loader=loader;
    }
    
    public int size(){
        return this.params.size();
    }

    public String getParameterValue(String clave){
        for(MemoryParameter p: this.params){
            if( p.getClave().equals(clave) ){
                return p.getValor();
            }
        }
        
        return null;
    }
    
    public MemoryParameter getParameter(String clave){
        for(MemoryParameter p: this.params){
            if( p.getClave().equals(clave) ){
                return p;
            }
        }
        
        return null;
    }
    
    public List<String> getParametersKeys(){
        ArrayList<String> keys=new ArrayList<String>();
        
        for(MemoryParameter p: this.params){
            keys.add(p.getClave());
        }
        
        return keys;
    }
    
    public void addParam(String clave, String valor, String detalle, String tipo){
        if(tipo!=null && tipo.equalsIgnoreCase("n")){
            addParam(clave, valor, detalle, ParameterType.NUMERICO);
        }
        else if(tipo!=null && tipo.equalsIgnoreCase("f")){
            addParam(clave, valor, detalle, ParameterType.FECHA);
        }
        else if(tipo!=null && tipo.equalsIgnoreCase("b")){
            addParam(clave, valor, detalle, ParameterType.BOLEANO);
        }
        else{
            addParam(clave, valor, detalle, ParameterType.CADENA);
        }
    }
    
    public void addParam(String clave, String valor, String detalle, ParameterType tipo){
        boolean existe=false;
        
        for(int i=0; i<this.params.size(); i++){
            if( this.params.get(i).getClave().equals(clave) ){
                this.params.get(i).setValor(valor);
                existe=true;
                break;
            }
        }        
        
        //si no existe
        if( !existe ){
            MemoryParameter p=new MemoryParameter();
            p.setClave(clave);
            p.setDescripcion(detalle);
            p.setValor(valor);
            p.setTipo(tipo);
            this.params.add(p);
        }
    }
    
    public boolean updateParam(String parameter, String value) throws Exception{
        if( this.loader.updateParameter(parameter, value) ){
            this.loader.loadParameters(this);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        
        sb.append("||--------------------------------  MemoryParameters  --------------------------------||\n");
        sb.append("Numero: ").append(this.params.size()).append("\n");
        for(MemoryParameter p: this.params){
            sb.append(p.getClave()).append("(").append(p.getTipo()).append("=").append(p.getValor()).append("     ['").append(p.getDescripcion()).append("']\n");
        }
        sb.append("||------------------------------------------------------------------------------------||\n");
        
        return sb.toString();
    }
}
