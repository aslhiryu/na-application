package neoAtlantis.utils.apps.web.jsf;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Clase que permite gestionar las excepciones en los JSF
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class BasicExceptionHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    // this injection handles jsf
    public BasicExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new BasicExceptionHandler(parent.getExceptionHandler());
        return handler;
    }

}