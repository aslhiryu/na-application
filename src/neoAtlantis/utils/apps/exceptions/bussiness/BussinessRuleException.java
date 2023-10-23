package neoAtlantis.utils.apps.exceptions.bussiness;

import neoAtlantis.utils.apps.exceptions.ApplicationException;

/**
 * Objeto que representa un error en una regla de negocio del aplicativo
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class BussinessRuleException extends ApplicationException {
    public static final int NOT_PERMITTED_VALUE=2001;
    public static final int DUPLICATE_INFORMATION=2002;
    
    public BussinessRuleException(String msg, BussinessRuleException ex){
        super(ex.getCode(), msg, ex);
    }
    
    public BussinessRuleException(String msg){
        super(ApplicationException.BUSSINESS_RULE, msg);
    }
    
    public BussinessRuleException(int code, String msg){
        super(code, msg);
    }
}
