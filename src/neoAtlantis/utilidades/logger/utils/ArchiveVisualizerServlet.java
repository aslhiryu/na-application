package neoAtlantis.utilidades.logger.utils;

import java.io.*;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ArchiveVisualizerServlet  extends HttpServlet{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        ArrayList<File> archivos=(ArrayList<File>)request.getSession().getAttribute(PageLogVisualizerServlet.LISTA_LOGS);
        LogVisualizer vis=null;
        int l=0;
        long m=(Long)request.getSession().getAttribute(PageLogVisualizerServlet.MAX_LOGS);

        //intenta reconocer el log solicitado
        try{
            l=Integer.parseInt( request.getParameter("arc") );
        }catch(Exception ex){};

        //intenta reconocer el max solicitado
        try{
            m=Long.parseLong( request.getParameter("max") );
            request.getSession().setAttribute(PageLogVisualizerServlet.MAX_LOGS, m);
        }catch(Exception ex){};

        try{
            //reviso si se descarga el archivo
            if( request.getParameter("descargar")!=null ){
                RequestDispatcher dispatcher=request.getRequestDispatcher("/descargador.slt?file="+archivos.get(l).getAbsolutePath());
                dispatcher.forward(request, response);
            }
            else{
                //cargo el archivo de propiedades
                Properties prop=PageLogVisualizerServlet.cargaConfiguracion(this.getServletContext());
                OutputStream out=response.getOutputStream();

                StringBuffer sb=new StringBuffer("");
                sb.append("<html>\n");
                sb.append("<head>\n");
                sb.append("<link type='text/css' rel='stylesheet' href='logs.css' />\n");
                sb.append("</head>\n");
                sb.append("<body>\n");
                out.write(sb.toString().getBytes("UTF-8"));

                if( archivos.get(l).exists() ){
                    vis=new LogVisualizer(archivos.get(l).getAbsolutePath(), m);

                    if( request.getParameter("restart")!=null ){
                        vis.resetLog();
                    }

                    out.write(vis.getDatos(true, true).getBytes("UTF-8"));
                }
                else{
                    out.write(("<font color='red'><b>No se puede acceder al archivo: "+archivos.get(l).getAbsolutePath()+"</b></font>").getBytes("UTF-8"));
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        this.doGet(request, response);
    }
}
