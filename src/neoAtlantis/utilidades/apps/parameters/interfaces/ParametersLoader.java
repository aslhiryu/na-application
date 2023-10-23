package neoAtlantis.utilidades.apps.parameters.interfaces;

import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameters;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface ParametersLoader {
    public void loadParameters(MemoryParameters params) throws Exception; 
    public boolean updateParameter(String parameter, String value) throws Exception;
}
