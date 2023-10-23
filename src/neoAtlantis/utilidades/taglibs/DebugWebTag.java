package neoAtlantis.utilidades.taglibs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import neoAtlantis.utilidades.debuger.utils.DebugerRequestHtml;

/**
 *
 * @author Alberto Sanchez(aslhiryu@gmail.com)
 */
public class DebugWebTag extends TagSupport  {
    private boolean contexto=false;
    
    @Override
    public int doStartTag() throws JspException {
        try{
            pageContext.getOut().print(DebugerRequestHtml.obtieneDebug((HttpServletRequest)this.pageContext.getRequest(), this.contexto));
        } catch (Exception e) {
            throw new JspException ("Error: IOException" + e.getMessage());
        }
        return SKIP_BODY;
    }
    
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
    
    public void setContexto(boolean c){
        this.contexto=c;
    }
}
