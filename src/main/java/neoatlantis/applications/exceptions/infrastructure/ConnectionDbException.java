package neoatlantis.applications.exceptions.infrastructure;

import neoatlantis.applications.exceptions.ApplicationException;

/**
 * Objeto que representa un error en la conexion de BD
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ConnectionDbException extends ApplicationException {
    public ConnectionDbException(String msg, Exception ex){
        super(ApplicationException.DB_CONNECTION, msg, ex);
    }
    
    public ConnectionDbException(String msg){
        super(ApplicationException.DB_CONNECTION, msg);
    }
}
