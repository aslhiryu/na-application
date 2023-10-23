package neoAtlantis.utils.apps.exceptions.bussiness;

/**
 * Objeto que representa un error porque el valor no es pertitido en la aplicacion
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class NotPermittedValueException extends BussinessRuleException{
    public NotPermittedValueException(String msg, BussinessRuleException ex){
        super(msg, ex);
    }
    
    public NotPermittedValueException(String msg){
        super(BussinessRuleException.NOT_PERMITTED_VALUE, msg);
    }
    
}
