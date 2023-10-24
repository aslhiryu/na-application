package neoatlantis.applications.printer;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import neoatlantis.accesscontroller.printer.exceptions.FormatterException;
import neoatlantis.applications.printer.interfaces.LogViewerPrinter;
import neoatlantis.applications.utils.LogViewer;
import neoatlantis.utils.pagination.UtilsPagination;
import neoatlantis.applications.web.UtilsApplicationBean;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.utils.ResourcesLoader;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class SimpleHtmlLogViewerPrinter implements LogViewerPrinter {
    public static final String REQUEST_KEY="HttpRequest";

    private LogViewer logV;
    
    @Override
    public Object printLogViewer(Map<String, Object> params) throws FormatterException {
        StringBuilder sb = new StringBuilder("");
        HttpServletRequest request;
        int piv, pivOri;
        
        //obtengo el request
        if( params.get(REQUEST_KEY)==null ){
            throw new FormatterException("No se proporciono el request para generar el objeto HTML.");
        }
        request=((HttpServletRequest)params.get(REQUEST_KEY));

        sb.append("<script src=\"").append( request.getContextPath() ).append(ResourcesLoader.PATH_UTILS_JS).append("\"></script>\n");
        sb.append("<form name=\"NA:ChangedDataList\" id=\"NA:ChangedDataList\"  method=\"post\">\n");
        
        if( this.logV==null ){
            this.logV=new LogViewer((String)request.getServletContext().getAttribute(ApplicationListener.LOG_PATH_KEY));
        }
        
        //reviso si se efectua algun movimiento sobre el log
        if( request.getParameter(UtilsApplicationBean.OPERATION_PARAM)!=null && request.getParameter(UtilsApplicationBean.OPERATION_PARAM).equals(LogViewerPrinter.NEXT_OPERATION) ){
            this.logV.nextPage();
        }
        else if( request.getParameter(UtilsApplicationBean.OPERATION_PARAM)!=null && request.getParameter(UtilsApplicationBean.OPERATION_PARAM).equals(LogViewerPrinter.PREVIOUS_OPERATION) ){
            this.logV.previousPage();
        }
        else{
            this.logV.reset();
        }        
        
        String[] regs=this.logV.readLines(UtilsPagination.getPageSise());
        
        if( this.logV.existError()!=null ){
            sb.append("<span class=\"NA_General_textoError\">").append(this.logV.existError()).append("</span>\n");
        }
        else{
            sb.append("<div class=\"NA_DataList_list\">\n");
            sb.append("<table>\n");
            sb.append("<tr>\n");
            sb.append("<th>Evento</th>\n");
            sb.append("<th>Fecha</th>\n");
            sb.append("<th>Elemento</th>\n");
            sb.append("</tr>\n");
            for(int i=0; regs!=null&&i<regs.length; i++){
                if( regs[i]==null || regs[i].isEmpty() ){
                    continue;
                }
                
                sb.append("<tr>\n");
                piv=regs[i].indexOf("]");
                sb.append("<td>").append( piv>0? regs[i].substring(1, piv): "").append("</td>\n");
                pivOri=piv+2;
                piv=regs[i].indexOf("]", pivOri);
                sb.append("<td>").append( piv>0? regs[i].substring(pivOri, piv): "").append("</td>\n");
                pivOri=piv+2;
                piv=regs[i].indexOf("]", pivOri);
                pivOri=piv+2;
                piv=regs[i].indexOf("]", pivOri);
                sb.append("<td>").append( piv>0? regs[i].substring(pivOri, piv): "").append("</td>\n");
                sb.append("</tr>\n");
                sb.append("<tr>\n");
                sb.append("<td colspan=\"3\">\n");
                sb.append("<a id=\"NA:LogTabView").append(i).append("\" href=\"javaScript:NALogShowDetail(").append(i).append(");\">Mostrar detalle</a><div style=\"display:none;\" id=\"NA:LogDetailView").append(i).append("\">").append( regs[i].substring(piv+2)).append("<div>\n");
                sb.append("</td>\n");
                sb.append("</tr>\n");
            }
            sb.append("</table>\n");

            sb.append("<div id=\"NA_Pagination\">\n");
            sb.append("<div class=\"NA_Pagination_backControls\">\n");
            if( this.logV.hasPrevious() ){
                sb.append("<a href=\"javaScript:NALogPreviousPage()\" id=\"NA:PreviousPageButton\" class=\"NA_Pagination_previousPageButton\" title=\"P&aacute;gina Anterior\">&nbsp;</a>\n");                
            }
            sb.append("</div>\n");
            sb.append("<div class=\"NA_Pagination_pages\">\n");
            sb.append("</div>\n");
            sb.append("<div class=\"NA_Pagination_nextControls\">\n");
            if( this.logV.hasNext() ){
                sb.append("<a href=\"javaScript:NALogNextPage()\" id=\"NA:NextPageButton\" class=\"NA_Pagination_nextPageButton\" title=\"P&aacute;gina Siguiente\">&nbsp;</a>\n");                
            }
            sb.append("</div>\n");
            sb.append("</div>\n");
            
            sb.append("</div>\n");
        }
        
        sb.append("<input type=\"hidden\" name=\"").append(UtilsApplicationBean.OPERATION_PARAM).append("\" id=\"").append(UtilsApplicationBean.OPERATION_PARAM).append("\" value=\"\" />\n");
        sb.append("</form>\n");

        return sb.toString();
    }

    
}
