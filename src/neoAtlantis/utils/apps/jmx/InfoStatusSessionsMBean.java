package neoAtlantis.utils.apps.jmx;

import java.util.List;
import javax.servlet.ServletContext;
import neoAtlantis.utils.apps.web.listeners.ApplicationListener;
import neoAtlantis.utils.apps.web.objects.RequestStatistics;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class InfoStatusSessionsMBean implements InfoStatusSessions{
    private static final Logger DEBUGGER=Logger.getLogger(InfoStatusSessionsMBean.class);

    private ServletContext context;
    private int ses;
    private int sesMax;
    
    public InfoStatusSessionsMBean(ServletContext sc){
        this.context=sc;
    }

    @Override
    public double getSessionLifetimeMinimal() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.SESSION_STATISTICS_KEY);
            
            return (sta!=null? sta.getMinimal(): 0);
        }
    }

    @Override
    public double getSessionLifetimeMaximum() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.SESSION_STATISTICS_KEY);
            
            return (sta!=null? sta.getMaximum(): 0);
        }
    }

    @Override
    public double getSessionLifetimeAverage() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.SESSION_STATISTICS_KEY);
            
            return (sta!=null? sta.getAverage(): 0);
        }
    }
    
    @Override
    public int getActiveSessions(){
        if(this.context==null  ){
            DEBUGGER.debug("Recupera las sesiones, pero aun no hay contexto");            
            return 0;
        }
        else{
            List lTmp=(List)this.context.getAttribute(ApplicationListener.SESSIONS_KEY);
            DEBUGGER.debug("Recupera las sesiones con un contexto existente");
            this.ses=(lTmp!=null? lTmp.size(): 0);
            
            if(this.ses>this.sesMax){
                this.sesMax=this.ses;
            }
            
            return this.ses;
        }
    }

    @Override
    public int getActiveSessionsMaximum(){
        return this.sesMax;
    }
}
