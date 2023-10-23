package neoAtlantis.utilidades.web.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class OrdenatorServlet extends HttpServlet {
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

        response.getOutputStream().write( generaOrdenamiento(request, p).getBytes() );
    }
    
    //--------------------------------------------------------------------------
    
    public static String generaOrdenamiento(HttpServletRequest request, String ligaActual){
        StringBuilder sb=new StringBuilder("");
        String[][] datosOrd=(String[][])request.getAttribute("na_order_cols");
        String ordActual=(request.getParameter("na_order")!=null? request.getParameter("na_order"): "");
        String ordTipo=(request.getParameter("na_descent")!=null? "desc": "asc");
        boolean isFiltrado=(request.getParameter("na_filter")!=null? true: false);
        int pagActual=1;

        try{
            pagActual=Integer.parseInt(request.getParameter("na_page"));
        }catch(Exception ex){}
        
        //dibujo el renglon
        sb.append("<tr class='NA-Ordanamiento-titulo'>\n");
            
        for(int k=0; datosOrd!=null&&k<datosOrd.length; k++){
            sb.append("<td>\n");
            
            if( datosOrd[k][1].equals("") ){
                sb.append(datosOrd[k][0]);
            }
            else{
                sb.append("<a href='").append(ligaActual).append("?na_page=").append(pagActual).append("&na_order=").append(datosOrd[k][1]).append((datosOrd[k][1].equals(ordActual)? (ordTipo.equals("asc")? "&na_descent": ""): "")).append(isFiltrado? "&na_filter": "").append("'>").append(datosOrd[k][0]).append("</a>");
                if( (ordActual.equals("") && datosOrd[k][2].equals("1")) ||(datosOrd[k][1].equals(ordActual) && ordTipo.equals("desc")) ){
                    sb.append("<img src='images/Down.gif' border='0' />");
                }
                else if( datosOrd[k][1].equals(ordActual) || (ordActual.equals("") && datosOrd[k][2].equals("0")) ){
                    sb.append("<img src='images/Top.gif' border='0' />");
                }
            }
            sb.append("\n</td>\n");
        }
        
        sb.append("</tr>\n");


        return sb.toString();
    }



}
