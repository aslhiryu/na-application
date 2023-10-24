package neoatlantis.applications.exceptions.infrastructure;

import neoatlantis.applications.exceptions.ApplicationException;

/**
 * Objeto que representa un error de registro duplicado
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class DuplicatedRegistryException extends ApplicationException{
    public DuplicatedRegistryException(String mensaje, Exception causa){
        super(ApplicationException.INVALID_DATA, mensaje, causa);
    }

    public DuplicatedRegistryException(String mensaje){
        super(ApplicationException.INVALID_DATA, mensaje);
    }
    
}
