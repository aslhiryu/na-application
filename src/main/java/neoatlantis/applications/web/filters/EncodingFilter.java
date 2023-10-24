package neoatlantis.applications.web.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.log4j.Logger;

/**
 * Clase que forza la codificación en el acceso a los recursos web
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class EncodingFilter implements Filter {
    private static final Logger DEBUGGER=Logger.getLogger(EncodingFilter.class);

    private String encoding = "utf-8";

    public EncodingFilter(){        
    }
    
    public EncodingFilter(String encoding){
        this.encoding=encoding;
    }
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        String encodingParam = fc.getInitParameter("encoding");

        if (encodingParam != null) {
            encoding = encodingParam;
        }
        DEBUGGER.debug("Se configura la aplicacion con codificación: "+encoding);
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        sr.setCharacterEncoding(encoding);
        sr1.setCharacterEncoding(encoding);
        fc.doFilter(sr, sr1);
    }

    @Override
    public void destroy() {
        // nothing todo
    }
}