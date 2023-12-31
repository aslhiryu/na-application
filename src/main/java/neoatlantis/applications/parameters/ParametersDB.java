package neoatlantis.applications.parameters;

import java.util.List;
import java.util.Properties;
import neoatlantis.entity.SimpleDAO;
import neoatlantis.applications.parameters.exceptions.MemoryParameterLoaderException;
import neoatlantis.applications.parameters.interfaces.MemoryParametersLoader;
import neoatlantis.applications.parameters.objects.MemoryParameters;
import neoatlantis.applications.parameters.objects.Parameter;
import neoatlantis.applications.printer.exceptions.ComponentInicializationException;
import neoatlantis.utils.configurations.ClassGenerator;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 * @param <E> Clase DAO que gestionara la interaccion con la BD
 */
public class ParametersDB<E extends SimpleDAO> implements MemoryParametersLoader {
    private static final Logger DEBUGER = Logger.getLogger(ParametersDB.class);

    protected E dao;
    

    
    //---------------------------------------------------------------------------------

    /**
     * Genera un ParametersLoader por Base de Datos, tomando los valores por default para los campos en la BD.
     * @param type Clase utilizada para gestionar los datos en la BD
     * @param config Properties con la configuración de la BD
     * @throws neoatlantis.applications.printer.exceptions.ComponentInicializationException
     */
    public ParametersDB(Class<E> type, Properties config) throws ComponentInicializationException {
        this.dao=(E)ClassGenerator.createInstance(type, new Object[]{config});
    }

    /**
     * Genera un ParametersLoader por Base de Datos, tomando los valores por default para los campos en la BD.
     * @param type Clase utilizada para gestionar los datos en la BD
     * @param jndi Nombre del dataSource a utilizar
     * @throws neoatlantis.applications.printer.exceptions.ComponentInicializationException
     */
    public ParametersDB(Class<E> type, String jndi) throws ComponentInicializationException {
        this.dao=(E)ClassGenerator.createInstance(type, new Object[]{jndi});
    }


    
    
    
    //---------------------------------------------------------------------------------

    @Override
    public void loadParameters(MemoryParameters params) throws MemoryParameterLoaderException {
        List<Parameter> lTmp;
        
        try{
            lTmp=this.dao.select(new Parameter());
            
            //si existen parametros
            for(int i=0; lTmp!=null&&i<lTmp.size(); i++){
                params.addParameter(lTmp.get(i).getParameter(), lTmp.get(i).getValue(), lTmp.get(i).getDescription(), lTmp.get(i).getType());
            }
        }
        catch(Exception ex){
            DEBUGER.fatal("No se logro recuperar los parametros.", ex);
            throw new MemoryParameterLoaderException(ex);
        }
    }

    @Override
    public boolean updateParameter(String parameter, String value) throws MemoryParameterLoaderException {
        Parameter to;
        
        try{
            to=new Parameter();
            to.setParameter(parameter);
            to.setValue(value);
            
            return this.dao.update(to);
        }
        catch(Exception ex){
            DEBUGER.fatal("No se logro actualizar el parametro '"+parameter+"'.", ex);
            throw new MemoryParameterLoaderException(ex);
        }
    }

}
