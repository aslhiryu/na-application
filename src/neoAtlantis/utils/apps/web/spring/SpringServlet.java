package neoAtlantis.utils.apps.web.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Objeto bas epara los servlet que utilicen los bean de spring
 * @author Hiryu (aslhiryu@gmail.com)
 */
public abstract class SpringServlet extends HttpServlet {
    protected AutowireCapableBeanFactory springContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        springContext = context.getAutowireCapableBeanFactory();
        springContext.autowireBean(this);
    }    
}

