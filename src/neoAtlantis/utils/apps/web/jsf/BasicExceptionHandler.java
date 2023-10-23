package neoAtlantis.utils.apps.web.jsf;

import java.util.Iterator;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utils.apps.web.listeners.ItopReporterListener;
import neoAtlantis.utils.apps.web.utils.ItopTicketGenerator;
import neoAtlantis.utils.apps.web.utils.ItopUtils;
import org.apache.log4j.Logger;

/**
 * Objeto que permite un manejo personalizado de las excepciones de Vista en la JSF
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class BasicExceptionHandler extends ExceptionHandlerWrapper {
    private static final Logger LOGGER=Logger.getLogger(BasicExceptionHandler.class);
    private static final String SERVLET_ERROR="javax.servlet.error.message";
    
    
    public  static final String SERVLET_EXCEPTION="javax.servlet.error.lastException";

    private ExceptionHandler wrapped;
    private String login="/index.jsp";
    private String error="/error.jsp";

    private Map<String,Object> configItop;
    
    
    BasicExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        String cTmp;
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            // get the exception from context
            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            //recupero el contexto d ela aplicacion
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            //si existe contexto recupero los valores configurados
            if( servletContext!=null ){
                cTmp=servletContext.getInitParameter("na.params.jsf.factory.LOGIN");
                if(cTmp!=null && !cTmp.isEmpty()){
                    this.login=cTmp;
                }
                cTmp=servletContext.getInitParameter("na.params.jsf.factory.ERROR");
                if(cTmp!=null && !cTmp.isEmpty()){
                    this.error=cTmp;
                }
                
                //recupera atributos para reportar errores a iTop
                if( this.configItop==null ){
                    this.configItop=ItopUtils.loadReportConfiguration(servletContext);
                }              
            }
            
            LOGGER.debug("Pagina de error: "+this.error);
            LOGGER.debug("Pagina de inicio: "+this.login);
            
            //here you do what ever you want with exception
            try {
                if (t instanceof ViewExpiredException) {
                    requestMap.put(SERVLET_ERROR, "Session expired, try again!");
                    fc.setViewRoot(fc.getApplication().getViewHandler().createView(fc,  this.login));
                    fc.getPartialViewContext().setRenderAll(true);
                    fc.renderResponse();
                    LOGGER.debug("Genera un error de vista, por falta de usuario.");
                }
                else if(t instanceof IllegalStateException || (t.getCause()!=null && t.getCause() instanceof IllegalStateException)){
                    LOGGER.info("Se genero una excepcion de cambio ilegal de estado.");
                }
                else{
                    //realiza reporte a iTop
                    if( servletContext.getAttribute(ItopReporterListener.REPORT_ACTIVE_KEY)!=null
                            && (Boolean)servletContext.getAttribute(ItopReporterListener.REPORT_ACTIVE_KEY) ){
                        LOGGER.debug("Se genera una excepción, procedo a generar un ticket en iTop");
                        (new ItopTicketGenerator(this.configItop, (HttpServletRequest)fc.getExternalContext().getRequest(), t)).start();
                    }

                    //genera elevento del error
                    requestMap.put(SERVLET_ERROR, "Error desconocido");
                    requestMap.put(SERVLET_EXCEPTION, t);
                    fc.setViewRoot(fc.getApplication().getViewHandler().createView(fc,  this.error));
                    fc.getPartialViewContext().setRenderAll(true);
                    fc.renderResponse();
                    LOGGER.error("Genera un error en el llamado del JSF.", t);
                }
            } finally {
                i.remove();
            }
        }
        
        getWrapped().handle();
    }
    
    
    // ----------------------------------------------------------------------------------------------------------------------------------------------------------
    
}
