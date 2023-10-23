package neoAtlantis.utils.apps.parameters.exceptions;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryParameterLoaderException extends Exception {
    public MemoryParameterLoaderException(Exception ex){
        super(ex);
    }
    
    public MemoryParameterLoaderException(String ex){
        super(ex);
    }

    public MemoryParameterLoaderException(String msg, Throwable ex){
        super(msg, ex);
    }
    
}
