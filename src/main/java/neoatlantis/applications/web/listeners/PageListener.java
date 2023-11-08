package neoatlantis.applications.web.listeners;

import java.util.Date;
import java.util.List;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import neoatlantis.applications.web.objects.ApplicationSession;
import neoatlantis.accesscontroller.objects.Browser;
import neoatlantis.accesscontroller.objects.OperatingSystem;
import neoatlantis.applications.web.objects.RequestStatistics;
import neoatlantis.applications.web.utils.ParameterCleaner;
import org.apache.log4j.Logger;

/**
 * Clase que pepara las paginas a desplegar
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PageListener implements ServletRequestListener {
    private static final Logger DEBUGGER=Logger.getLogger(PageListener.class);

    //Nombre de los parametros de formularios de las paginas
    public static final String ORDER_PARAM = "NA_DataOrder";
    public static final String PAGE_PARAM = "NA_DataPage";
    public static final String DESCENDING_PARAM = "NA_DataModeOrder";
    public static final String FILTER_NAMES_PARAM = "NA_FilterName";
    public static final String FILTER_VALUES_PARAM = "NA_FilterValue";
    //Nombre de las llaves de valores en el request
    public static final String FORM_NAME_KEY="na.app.page.formName";
    public static final String COLUMN_KEY="na.app.page.column";
    public static final String TEXT_KEY="na.app.page.displayText";
    public static final String ORDER_TYPE_KEY="na.app.page.orderType";
    public static final String STATE_KEY = "na.app.page.state";
    public static final String LOAD_TIME_KEY="na..app.page.loadTime";

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request=(HttpServletRequest)sre.getServletRequest();
        
        DEBUGGER.debug( "==> "+ParameterCleaner.getRelativeExtension(request.getServletPath())+", "+request.getRequestURL());
        //reviso que solo se traten de paginas
        if( ParameterCleaner.isPage(ParameterCleaner.getRelativeExtension(request.getServletPath())) ){
            SessionListener.setIP(request);
            SessionListener.setOS(request);
            sre.getServletRequest().setAttribute(LOAD_TIME_KEY, (new Date()).getTime());
            updateActivity((List<ApplicationSession>)sre.getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY), request.getSession().getId());
        }        
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request=(HttpServletRequest)sre.getServletRequest();
        
        if( ParameterCleaner.isPage(ParameterCleaner.getRelativeExtension(request.getServletPath())) ){
            Long ini=(Long)sre.getServletRequest().getAttribute(LOAD_TIME_KEY);

            if( ini!=null ){
                double t=((double)((new Date()).getTime()-ini)/1000);
                DEBUGGER.info("Tiempo de generación de la pagina '"+request.getServletPath()+"': "+t+" segs");
                
                //actualiza los datos de la estadistica
                RequestStatistics sta=(RequestStatistics)sre.getServletContext().getAttribute(ApplicationListener.PAGE_STATISTICS_KEY);
                if(sta!=null){
                    sta.updateStatistics(t);
                }
            }
            if(request.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY)!=  null){
                SessionListener.validateSession((List)request.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY), request.getSession().getMaxInactiveInterval());
            }
        }
    }

    /**
     * Actualiza el estado de la sesión actual
     * @param sesiones Lista de sesiones activas
     * @param sesion  Id de la sesion a actualizar
     */
    public void updateActivity(List<ApplicationSession> sesiones, String sesion) {
        for(int i=0; sesiones!=null && i<sesiones.size(); i++){
            if( sesiones.get(i).getId().equals(sesion)!=false ) {
                sesiones.get(i).setLastActivity(new Date());
                DEBUGGER.info("Actualizo la actividad de la sesion '"+sesion+"'.");
            }
        }
    }
    
    
    
    
    // -------------------------------------------------------------------------
    
    public static OperatingSystem getOperatingSystem(HttpServletRequest request){
        //recupero las sesiones activas
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)request.getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(int i=0; sesTmp!=null&&i<sesTmp.size(); i++){
            if( sesTmp.get(i).getHttpSession()!=null && request.getSession().getId().equals( sesTmp.get(i).getHttpSession().getId() ) ){
                return sesTmp.get(i).getOs();
            }
        }
        
        return OperatingSystem.OTRO;
    }

    public static Browser getBrowser(HttpServletRequest request){
        //recupero las sesiones activas
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)request.getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(int i=0; sesTmp!=null&&i<sesTmp.size(); i++){
            if( sesTmp.get(i).getHttpSession()!=null && request.getSession().getId().equals( sesTmp.get(i).getHttpSession().getId() ) ){
                return sesTmp.get(i).getBrowser();
            }
        }
        
        return Browser.OTRO;
    }

}
