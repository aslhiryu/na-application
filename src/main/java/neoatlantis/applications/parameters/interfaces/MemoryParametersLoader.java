package neoatlantis.applications.parameters.interfaces;

import neoatlantis.applications.parameters.exceptions.MemoryParameterLoaderException;
import neoatlantis.applications.parameters.objects.MemoryParameters;

/**
 * Interfaz que define la funcionalidad para un cargador de parametros en memoria
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface MemoryParametersLoader {
    public void loadParameters(MemoryParameters params) throws MemoryParameterLoaderException; 
    public boolean updateParameter(String parameter, String value) throws MemoryParameterLoaderException;
}
