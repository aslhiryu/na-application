package neoatlantis.applications.printer.exceptions;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ComponentInicializationException extends Exception {
    public ComponentInicializationException(Exception ex){
        super(ex);
    }    

    public ComponentInicializationException(String ex){
        super(ex);
    }    
}