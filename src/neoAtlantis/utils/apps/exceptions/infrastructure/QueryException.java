package neoAtlantis.utils.apps.exceptions.infrastructure;

import neoAtlantis.utils.apps.exceptions.ApplicationException;

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

