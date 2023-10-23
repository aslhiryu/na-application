package neoAtlantis.utils.apps.utils;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class UtilsPagination {
    private static int pageSize=10;
    private static int visiblePages=5;
    

    
    
    
    // -------------------------------------------------------------------------
    
    public static int getPageSise(){
        return pageSize;
    }
    
    public static void setPageSize(int t){
        pageSize=t;
    }
    
    public static int calculatePages(int regs){
        if(regs==0){
            return 0;
        }
        
        int p=regs/pageSize;
        
        if(regs%pageSize>0){
            p++;
        }
        
        return p;
    }
    
    public static String printHtmlPagination(int pages, int actual){
        StringBuilder sb = new StringBuilder("");
        int inicio=actual-visiblePages;
        int fin=actual+visiblePages;
        
        if( inicio<1 ){
            inicio=1;
        }
        if(fin>pages){
            fin=pages;
        }
        
        sb.append("<div id=\"NA_Pagination\">\n");
        sb.append("<div class=\"NA_Pagination_registries\">\n");
        sb.append("</div>\n");
        sb.append("<div class=\"NA_Pagination_backControls\">\n");
        if(actual>1){
            sb.append("<a href=\"javaScript:NADefaultDataChangePage(1)\" id=\"NA:FirstPageButton\" class=\"NA_Pagination_firstPageButton\" title=\"P&aacute;gina Inicial\">&nbsp;</a>\n");                
            sb.append("<a href=\"javaScript:NADefaultDataChangePage(").append(actual).append("-1)\" id=\"NA:PreviousPageButton\" class=\"NA_Pagination_previousPageButton\" title=\"P&aacute;gina Anterior\">&nbsp;</a>\n");                
        }
        sb.append("</div>\n");

        sb.append("<div class=\"NA_Pagination_pages\">\n");
        if(inicio>1){
            sb.append(" ... ");
        }        
        for(int i=inicio; i<=fin; i++){
            if(i==actual){
                sb.append("<em>").append(i).append("</em>\n");
            }
            else{
                sb.append("<a href=\"javaScript:NADefaultDataChangePage(").append(i).append(")\"  title=\"P&aacute;gina ").append(i).append("\">").append(i).append("</a>\n");
            }
        }
        if(fin<pages){
            sb.append(" ... ");
        }        
        sb.append("</div>\n");
        
        sb.append("<div class=\"NA_Pagination_nextControls\">\n");
        if(actual<pages){
            sb.append("<a href=\"javaScript:NADefaultDataChangePage(").append(actual).append("+1)\" id=\"NA:NextPageButton\" class=\"NA_Pagination_nextPageButton\" title=\"P&aacute;gina Siguiente\">&nbsp;</a>\n");                
            sb.append("<a href=\"javaScript:NADefaultDataChangePage(").append(pages).append(")\" id=\"NA:LastPageButton\" class=\"NA_Pagination_lastPageButton\" title=\"&Uacute;ltima P&aacute;gina\">&nbsp;</a>\n");                
        }
        sb.append("</div>\n");
        sb.append("</div>\n");
        
        return sb.toString();
    }
}
