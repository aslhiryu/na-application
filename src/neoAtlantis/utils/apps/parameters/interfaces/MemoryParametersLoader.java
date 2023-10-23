package neoAtlantis.utils.apps.parameters.interfaces;

import neoAtlantis.utils.apps.parameters.exceptions.MemoryParameterLoaderException;
import neoAtlantis.utils.apps.parameters.objects.MemoryParameters;

/**
 * Interfaz que define la funcionalidad para un cargador de parametros en memoria
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface MemoryParametersLoader {
    public void loadParameters(MemoryParameters params) throws MemoryParameterLoaderException; 
    public boolean updateParameter(String parameter, String value) throws MemoryParameterLoaderException;
}
