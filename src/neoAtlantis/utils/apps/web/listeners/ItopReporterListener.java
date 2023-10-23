package neoAtlantis.utils.apps.web.listeners;

import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import neoAtlantis.utils.apps.web.ItopIncidentControlServlet;
import neoAtlantis.utils.apps.web.filters.ItopIncidentFilter;
import neoAtlantis.utils.apps.web.utils.ItopUtils;
import org.apache.log4j.Logger;

/**
 * Objeto que publica la configuración y elementos necesarios para generar incidencias al iTop
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ItopReporterListener implements ServletContextListener {
    private static final Logger LOGGER=Logger.getLogger(ItopReporterListener.class);
    public static final String REPORT_ACTIVE_KEY="na.util.app.itop.reporting";
    public static final String PATH_COMTROL_SERVICE="/neoAtlantis/resources/web/iTopControl.service";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String,Object> config;
        
        try{
            //genera el filtro
            LOGGER.debug("Intento publicar el filtro de reportes para iTop");
            config=ItopUtils.loadReportConfiguration(sce.getServletContext());
            LOGGER.debug("Configuración para el filtro de iTop: "+config);
            javax.servlet.FilterRegistration.Dynamic dim=sce.getServletContext().addFilter("ItopReportingNA", new ItopIncidentFilter(config));
            dim.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
            LOGGER.debug("Filtro de reportes para iTop inicializado");
        }
        catch(Exception ex){
            LOGGER.warn("No se logro cargar el filtro para recolectr los incidentes de iTop", ex);
        }
        
        //genera la valiable para gestionar la actividad del filtro
        sce.getServletContext().setAttribute(REPORT_ACTIVE_KEY, true);
        
        try{
            //publica el servlet de control para el filtro
            javax.servlet.ServletRegistration.Dynamic dimRequest=sce.getServletContext().addServlet("ItopReportingControlNA", new ItopIncidentControlServlet());
            dimRequest.addMapping(PATH_COMTROL_SERVICE);
            dimRequest.setAsyncSupported(true);
            LOGGER.debug("Servlet de control de reportes para iTop inicializado");
        }
        catch(Exception ex){
            LOGGER.warn("No se logro cargar el servlet de control de incidentes de iTop", ex);
        }        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
}
