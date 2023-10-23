package neoAtlantis.utils.apps.exceptions.bussiness;

/**
 * Objeto que representa un error porque ya existe la información en la aplicación
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class AlreadyExistInformationException extends BussinessRuleException{
    public AlreadyExistInformationException(String msg, BussinessRuleException ex){
        super(msg, ex);
    }
    
    public AlreadyExistInformationException(String msg){
        super(BussinessRuleException.DUPLICATE_INFORMATION, msg);
    }
    
}
