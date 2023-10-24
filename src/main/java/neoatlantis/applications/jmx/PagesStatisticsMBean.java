package neoatlantis.applications.jmx;

import javax.servlet.ServletContext;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.objects.RequestStatistics;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PagesStatisticsMBean implements PagesStatistics{

    private ServletContext context;
    
    public PagesStatisticsMBean(ServletContext sc){
        this.context=sc;
    }

    @Override
    public double getResponseMinimalTime() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.PAGE_STATISTICS_KEY);
            
            return (sta!=null? sta.getMinimal(): 0);
        }
    }

    @Override
    public double getResponseMaximumTime() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.PAGE_STATISTICS_KEY);
            
            return (sta!=null? sta.getMaximum(): 0);
        }
    }

    @Override
    public double getResponseAverageTime() {
        if(this.context==null  ){
            return 0;
        }
        else{
            RequestStatistics sta=(RequestStatistics)this.context.getAttribute(ApplicationListener.PAGE_STATISTICS_KEY);
            
            return (sta!=null? sta.getAverage(): 0);
        }
    }
    
}
