package neoAtlantis.utils.apps.exceptions.infrastructure;

import neoAtlantis.utils.apps.exceptions.ApplicationException;

/**
 * Objeto que representa un error  de un dato invalido
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class InvalidDataException extends ApplicationException {
    public InvalidDataException(String msg, Exception ex){
        super(ApplicationException.INVALID_DATA, msg, ex);
    }
    
    public InvalidDataException(String msg){
        super(ApplicationException.INVALID_DATA, msg);
    }
}
