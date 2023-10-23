package neoAtlantis.utilidades.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class EncodingFilter implements Filter {
    private String encoding = "utf-8";

    @Override
    public void init(FilterConfig fc) throws ServletException {
        String encodingParam = fc.getInitParameter("encoding");

        if (encodingParam != null) {
            encoding = encodingParam;
        }
    }

    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        sr.setCharacterEncoding(encoding);
        fc.doFilter(sr, sr1);
    }

    public void destroy() {
        // nothing todo
    }
}