package neoAtlantis.utilidades.web.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PaginatorServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String p="#";
        int n=1;

        if( request.getAttribute("na_actual_page")!=null ){
            p=(String)request.getAttribute("na_actual_page");
        }
        else if( request.getParameter("na_actual_page")!=null ){
            p=request.getParameter("na_actual_page");
        }

        if( request.getAttribute("na_pages")!=null ){
            try{
                n=Integer.parseInt((String)request.getAttribute("na_pages"));
            }catch(Exception ex){}
        }
        else if( request.getParameter("na_pages")!=null ){
            try{
                n=Integer.parseInt(request.getParameter("na_pages"));
            }catch(Exception ex){}
        }
        
        response.getOutputStream().write( generaPaginacion(request, n, p).getBytes() );
    }
    
    
    public static String generaPaginacion(HttpServletRequest request, int paginas, String ligaActual){
        return generaPaginacion(request, paginas, 3, ligaActual);
    }
    
    //--------------------------------------------------------------------------
    
    public static String generaPaginacion(HttpServletRequest request, int paginas, int pagVisibles, String ligaActual){
        StringBuilder sb=new StringBuilder("");
        int pagActual=1;
        int recPagIni;
        int recPagFin;
        String ordActual=(request.getParameter("na_order")!=null? request.getParameter("na_order"): "");
        String ordTipo=(request.getParameter("na_descent")!=null? "desc": "asc");
        boolean isFiltrado=(request.getParameter("na_filter")!=null? true: false);

        //reviso se pasaron el parametro de pagina
        try{
            pagActual=Integer.parseInt(request.getParameter("na_page"));
        }catch(Exception ex){}
        
        //
        if( pagVisibles<3 ) pagVisibles=3;

        //defino cuantas paginas va a mostrar antes y despues
        recPagIni=pagActual-pagVisibles;
        recPagFin=pagActual+pagVisibles;
        if( recPagIni<1 ) recPagIni=1;
        if( recPagFin>paginas ) recPagFin=paginas;
        
        //dibujo la tabla
        sb.append("<div id='NA-Paginacion-espacio'>\n");
            
        if( pagActual>1 ){
            sb.append("<a href='").append(ligaActual).append("?na_page=1&na_order=").append(ordActual).append(ordTipo.equals("desc")? "&na_descent": "").append(isFiltrado? "&na_filter": "").append("' title='Primera'><div id='NA-Paginacion-primera'></div></a>&nbsp;\n");
            sb.append("<a href='").append(ligaActual).append("?na_page=").append(pagActual-1).append("&na_order=").append(ordActual).append(ordTipo.equals("desc")? "&na_descent": "").append(isFiltrado? "&na_filter": "").append("' title='Anterior'><div id='NA-Paginacion-anterior'></div></a>\n");
        }
        sb.append("&nbsp;\n");

        if(recPagIni!=1){
            sb.append("...\n");
        }
        for(int j=recPagIni; j<=recPagFin; j++){
            if( pagActual!=j ){
                sb.append("<a href='").append(ligaActual).append("?na_page=").append(j).append("&na_order=").append(ordActual).append(ordTipo.equals("desc")? "&na_descent": "").append(isFiltrado? "&na_filter": "").append("'>").append(j).append("</a> \n");
            }
            else{
                sb.append(j).append(" \n");
            }
        }
        if(recPagFin!=paginas){
            sb.append("...\n");
        }

        sb.append("&nbsp;\n");
        if( pagActual<paginas ){
            sb.append("<a href='").append(ligaActual).append("?na_page=").append(pagActual+1).append("&na_order=").append(ordActual).append(ordTipo.equals("desc")? "&na_descent": "").append(isFiltrado? "&na_filter": "").append("' title='Siguiente'><div id='NA-Paginacion-siguiente'></div></a>&nbsp;\n");
            sb.append("<a href='").append(ligaActual).append("?na_page=").append(paginas).append("&na_order=").append(ordActual).append(ordTipo.equals("desc")? "&na_descent": "").append(isFiltrado? "&na_filter": "").append("' title='Ultima'><div id='NA-Paginacion-ultima'></div></a>\n");
        }
        
        sb.append("</div>\n");

        //por si ecxiste un filtro
        if( isFiltrado ){
            sb.append("<script>\n");
            sb.append("despliegaFiltros();\n");
            sb.append("</script>\n");
        }

        return sb.toString();
    }
}
