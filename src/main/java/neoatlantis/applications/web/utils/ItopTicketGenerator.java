package neoatlantis.applications.web.utils;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author desarrollo.alberto
 */
public class ItopTicketGenerator extends Thread {
    private static final Logger LOGGER=Logger.getLogger(ItopTicketGenerator.class);

    private Map<String,Object> config;
    private HttpServletRequest request;
    private Throwable exception;
    
    public ItopTicketGenerator(Map<String,Object> config, HttpServletRequest request, Throwable exception){
        this.config=config;
        this.request=request;
        this.exception=exception;
    }
    
    @Override
    public void run(){
        LOGGER.debug("Inicia el proceso para reportar un incidente en iTop");
        ItopUtils.reportErrorToItop(this.config, this.request, this.exception);
    }
}
