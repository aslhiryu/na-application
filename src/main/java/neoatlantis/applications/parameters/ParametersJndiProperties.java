package neoatlantis.applications.parameters;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import neoatlantis.applications.parameters.exceptions.MemoryParameterLoaderException;
import neoatlantis.applications.parameters.interfaces.MemoryParametersLoader;
import neoatlantis.applications.parameters.objects.MemoryParameters;
import neoatlantis.applications.parameters.objects.ParameterType;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ParametersJndiProperties implements MemoryParametersLoader{
    private static final Logger DEBUGER = Logger.getLogger(ParametersJndiProperties.class);
    
    private String jndi;

    /**
     * Genera un ParametersLoader utilizando un properties en el JNDI.
     * @param jndi Clave del recurso properties en el JNDI a utilizar
     */
    public ParametersJndiProperties(String jndi){
        this.jndi=jndi;
    }
    
    
    @Override
    public void loadParameters(MemoryParameters params) throws MemoryParameterLoaderException {
        try{
            Context initialContext = new InitialContext();
            Properties props = (Properties)initialContext.lookup(this.jndi);
            DEBUGER.debug("Se encontro un properties '"+this.jndi+"' con la siguiente información: "+props);
            
            //cargo la información d elos parametros
            if( props!=null ){
                for(Object k: props.keySet()){
                    params.addParameter(""+k, props.getProperty(""+k), "N/D", ParameterType.STRING);
                }
            }
        }
        catch(NamingException ex){
            throw new MemoryParameterLoaderException("No se logro acceder al JNDI context.", ex);
        }
    }

    @Override
    public boolean updateParameter(String parameter, String value) throws MemoryParameterLoaderException {
        try{
            Context initialContext = new InitialContext();
            Properties props = (Properties)initialContext.lookup(this.jndi);
            DEBUGER.debug("Se encontro un properties '"+this.jndi+"' con la siguiente información: "+props);
            
            //valido si realmente existe el parametro
            if( props!=null && props.contains(parameter) ){
                //actualizo el valor
                props.setProperty(parameter, value);
                //actualizo la informacion del JNDI
                initialContext.rebind(this.jndi, props);
                return true;
            }
        }
        catch(NamingException ex){
            throw new MemoryParameterLoaderException("No se logro acceder al JNDI context.", ex);
        }
        
        return false;
    }
    
}
