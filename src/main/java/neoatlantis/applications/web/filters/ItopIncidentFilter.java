package neoatlantis.applications.web.filters;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import neoatlantis.applications.web.listeners.ItopReporterListener;
import neoatlantis.applications.web.utils.ItopTicketGenerator;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ItopIncidentFilter implements Filter {
    private static final Logger LOGGER=Logger.getLogger(ItopIncidentFilter.class);
    
    public static final String REPORT_ITOP="report";
    public static final String ITOP_URL="url";
    public static final String ITOP_USER="user";
    public static final String ITOP_PASS="pass";
    public static final String SERVICE_ID="service";
    public static final String CALLER_ID="caller";
    public static final String CI_ID="ci";
    public static final String ORG_ID="organization";
    
    private Map<String,Object> config;
    
    public ItopIncidentFilter(Map<String,Object> config){
        this.config=config;
    }
    
    /**
     * Carga la configuracion establecida para configurar el entorno
     * @param config
     * @throws ServletException 
     */
    @Override
    public void init(FilterConfig config) throws ServletException {        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //RestServiceClient client;

        try{
            chain.doFilter(request, response);
        }
        catch(Throwable ex){
            //valido si esta activo la generacion de reportes
            if( request.getServletContext().getAttribute(ItopReporterListener.REPORT_ACTIVE_KEY)!=null
                    && (Boolean)request.getServletContext().getAttribute(ItopReporterListener.REPORT_ACTIVE_KEY) ){
                LOGGER.debug("Se genera una excepción, procedo a generar un ticket en iTop");
                (new ItopTicketGenerator(this.config, (HttpServletRequest)request, ex)).start();
            }
            
            throw ex;
        }
    }

    @Override
    public void destroy() {
        // nothing todo
    }
    

    
}
