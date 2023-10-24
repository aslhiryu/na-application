package neoatlantis.applications.exceptions.infrastructure;

import neoatlantis.applications.exceptions.ApplicationException;

/**
 * Objeto que representa un error  en el query del  SQL
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class QueryException extends ApplicationException {
    public QueryException(String msg, Exception ex){
        super(ApplicationException.DB_QUERY, msg, ex);
    }
    
    public QueryException(String msg){
        super(ApplicationException.DB_QUERY, msg);
    }
}

