package neoAtlantis.utils.apps.printer;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import static neoAtlantis.utils.apps.printer.SimpleHtmlParameterAdministratorPrinter.REQUEST_KEY;
import neoAtlantis.utils.apps.printer.exceptions.FormatterException;
import neoAtlantis.utils.apps.printer.interfaces.NavigationPrinter;
import neoAtlantis.utils.apps.web.listeners.PageListener;
import neoAtlantis.utils.apps.web.objects.NavigationalState;
import neoAtlantis.utils.data.DataUtils;

/**
 *
 * @author desarrollo.alberto
 */
public class SimpleHtmlNavigationPrinter implements NavigationPrinter {
    private int visiblePages;
    
    public SimpleHtmlNavigationPrinter(int visible){
        this.visiblePages=visible;
    }
    
    public SimpleHtmlNavigationPrinter(){
        this(5);
    }

    @Override
    public Object printOrderOption(Map<String,Object> params) throws FormatterException{
        StringBuilder sb = new StringBuilder("");
        HttpServletRequest request;
        NavigationalState estado;
        boolean desc;
        String formName;
        String column;
        String label;
        
        if( params.get(REQUEST_KEY)==null ){
            throw new FormatterException("No se proporciono el request para generar el objeto HTML.");
        }
        else if( params.get(PageListener.FORM_NAME_KEY)==null ){
            throw new FormatterException("No se proporciono el nombre de la forma para generar el objeto HTML.");
        }
        else if( params.get(PageListener.COLUMN_KEY)==null ){
            throw new FormatterException("No se proporciono la columna de ordenamiento para generar el objeto HTML.");
        }
        else if( params.get(PageListener.TEXT_KEY)==null ){
            throw new FormatterException("No se proporciono la etiqueta para generar el objeto HTML.");
        }

        //obtengo el request
        request=((HttpServletRequest)params.get(REQUEST_KEY));
        estado=(NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY);
        formName=((String)params.get(PageListener.FORM_NAME_KEY));
        column=((String)params.get(PageListener.COLUMN_KEY));
        label=((String)params.get(PageListener.TEXT_KEY));
        
        if( estado!=null ){
                desc = false;
                
                //si es la columna deseada                
                if (estado.getOrder()[0] .equalsIgnoreCase(column)){
                    sb.append("<div class=\"NA_DataList_").append(estado.isDescedent() ? "descent" : "ascent").append("Order\"></div>");
                    desc = !estado.isDescedent();
                }                                        
                sb.append("<a href=\"javaScript:NADataChangeOrder('").append(column).append("', ").append(desc).append(", '").append(formName).append("')\">")
                    .append(label)
                    .append("</a>");
        }
        else{
            throw new FormatterException("No existe un NavigationalState para generar el objeto HTML.");
        }
        
        return sb.toString();
    }
    
    @Override
    public Object printPagination(Map<String, Object> params) throws FormatterException {
        StringBuilder sb = new StringBuilder("");
        HttpServletRequest request;
        NavigationalState estado;
        int pag=1;
        String formName;
        int inicio;
        int fin;
        String order;
        String orderMode;
        
        if( params.get(REQUEST_KEY)==null ){
            throw new FormatterException("No se proporciono el request para generar el objeto HTML.");
        }
        else if( params.get(PageListener.FORM_NAME_KEY)==null ){
            throw new FormatterException("No se proporciono el nombre de la forma para generar el objeto HTML.");
        }
        //obtengo el request
        request=((HttpServletRequest)params.get(REQUEST_KEY));
        estado=(NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY);
        formName=((String)params.get(PageListener.FORM_NAME_KEY));
        
        if( estado!=null ){
            inicio=estado.getActualPage()-visiblePages;
            fin=estado.getActualPage()+visiblePages;
            pag=estado.getActualPage();
            order=DataUtils.ArrayJoin(estado.getOrder(), ",");
            orderMode=""+estado.isDescedent();

            //corrige posibles problemas d einicio y fin
            if( inicio<1 ){
                inicio=1;
            }
            if(fin>estado.getPages()){
                fin=estado.getPages();
            }

            sb.append("<div id=\"NA_Pagination\">\n");
            sb.append("<div class=\"NA_Pagination_registries\">").append( estado.getRegistries() ).append(" Registros encontrados</div>\n");
            sb.append("<div class=\"NA_Pagination_backControls\">\n");
            if(estado.getActualPage()>1){
                sb.append("<a href=\"javaScript:NADataChangePage(1, '").append(formName).append("')\" id=\"NA:FirstPageButton\" class=\"NA_Pagination_firstPageButton\" title=\"P&aacute;gina Inicial\">&nbsp;</a>\n");                
                sb.append("<a href=\"javaScript:NADataChangePage(").append(estado.getActualPage()).append("-1, '").append(formName).append("')\" id=\"NA:PreviousPageButton\" class=\"NA_Pagination_previousPageButton\" title=\"P&aacute;gina Anterior\">&nbsp;</a>\n");                
            }
            sb.append("</div>\n");

            sb.append("<div class=\"NA_Pagination_pages\">\n");
            if(inicio>1){
                sb.append(" ... ");
            }        
            for(int i=inicio; i<=fin; i++){
                if(i==estado.getActualPage()){
                    sb.append("<em>").append(i).append("</em>\n");
                }
                else{
                    sb.append("<a href=\"javaScript:NADataChangePage(").append(i).append(", '").append(formName).append("')\"  title=\"P&aacute;gina ").append(i).append("\">").append(i).append("</a>\n");
                }
            }
            if(fin<estado.getPages()){
                sb.append(" ... ");
            }        
            sb.append("</div>\n");

            sb.append("<div class=\"NA_Pagination_nextControls\">\n");
            if(estado.getActualPage()<estado.getPages()){
                sb.append("<a href=\"javaScript:NADataChangePage(").append(estado.getActualPage()).append("+1, '").append(formName).append("')\" id=\"NA:NextPageButton\" class=\"NA_Pagination_nextPageButton\" title=\"P&aacute;gina Siguiente\">&nbsp;</a>\n");                
                sb.append("<a href=\"javaScript:NADataChangePage(").append(estado.getPages()).append(", '").append(formName).append("')\" id=\"NA:LastPageButton\" class=\"NA_Pagination_lastPageButton\" title=\"&Uacute;ltima P&aacute;gina\">&nbsp;</a>\n");                
            }
            sb.append("</div>\n");
            sb.append("<input type=\"hidden\" id=\"").append(PageListener.PAGE_PARAM).append("\" name=\"").append(PageListener.PAGE_PARAM).append("\" value=\"").append(pag).append("\" />\n");
            sb.append("<input type=\"hidden\" id=\"").append(PageListener.ORDER_PARAM).append("\" name=\"").append(PageListener.ORDER_PARAM).append("\" value=\"").append(order).append("\" />\n");
            sb.append("<input type=\"hidden\" id=\"").append(PageListener.DESCENDING_PARAM).append("\" name=\"").append(PageListener.DESCENDING_PARAM).append("\" value=\"").append(orderMode).append("\" />\n");
            sb.append("</div>\n");
        }
        else{
            throw new FormatterException("No existe un NavigationalState para generar el objeto HTML.");
        }
        
        return sb.toString();
    }
    
}
