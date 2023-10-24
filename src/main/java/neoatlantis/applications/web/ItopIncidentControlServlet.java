package neoatlantis.applications.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neoatlantis.applications.web.listeners.ItopReporterListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ItopIncidentControlServlet extends HttpServlet {
    private static final Logger LOGGER=Logger.getLogger(ItopIncidentControlServlet.class);
    
    public static final String ACTIVATION_PARAM="na:itopReport";
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        //valida si se proporciona la variable de activacion
        if( request.getParameter(ACTIVATION_PARAM)!=null ){
            if( request.getParameter(ACTIVATION_PARAM).equalsIgnoreCase("enable") ){
                request.getServletContext().setAttribute(ItopReporterListener.REPORT_ACTIVE_KEY, true);
                LOGGER.info("Habilita los reportes de incidentes al iTop");
            }
            if( request.getParameter(ACTIVATION_PARAM).equalsIgnoreCase("disable") ){
                request.getServletContext().setAttribute(ItopReporterListener.REPORT_ACTIVE_KEY, false);
                LOGGER.info("Inhabilita los reportes de incidentes al iTop");
            }
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        this.doGet(request, response);
    }
}
