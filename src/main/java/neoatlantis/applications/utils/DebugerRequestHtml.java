package neoatlantis.applications.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.listeners.PageListener;
import org.apache.log4j.Logger;

/**
 * Debuger que genera un html con infomación sobre un <i>Request</i> HTML.
 * @version 1.0
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class DebugerRequestHtml {
    private static final Logger DEBUGER=Logger.getLogger(DebugerRequestHtml.class);
    
    /**
     * Escribe el debug de variables a un flujo en especifico
     * @param out Flujo de salida
     * @param req Request del que se desa la informacion
     * @throws IOException 
     */
    public static void displayDebug(OutputStream out, HttpServletRequest req) throws IOException{
        out.write( getDebug(req).getBytes() );
    }
    
    /**
     * enera un html con la informacion sobre atributos y parametros de un <i>Request</i>.
     * @param req Request del que se desa la informacion
     * @return 
     */
    public static String getDebug(HttpServletRequest req){
        return getDebug(req, false);
    }
    

    /**
     * Genera un html con la informacion sobre atributos y parametros de un <i>Request</i>.
     * @param request Request del que se desa la informacion
     * @return Html con la informacion
     */
    public static String getDebug(HttpServletRequest request, boolean validaSession){
        StringBuilder sb=new StringBuilder();
        HttpSession ses;
        ServletContext app;
        Enumeration<String> eTmp;
        String cTmp;
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy HH:mm:ss");

        if( request!=null && (!validaSession || (validaSession && request.getSession().getServletContext().getAttribute(ApplicationListener.DEBUG_KEY)!=null)) ){
            sb.append("<br /><br /><hr />").append(System.getProperty("line.separator"));

            sb.append("<span style='font-size:9px;'>");
            
            //despliega info del tiempo de carga
            try{
                long ti=Long.parseLong(""+request.getAttribute(PageListener.LOAD_TIME_KEY));
                sb.append("<span style='font-size:14px;'><b>[TIEMPO CARGA]:</b></span><br />");
                sb.append( ((double)((new Date()).getTime()-ti))/1000 ).append(" segs.<br /><br />");
            }catch(Exception ex){}
            
            //despliega información sobre la aplicacion
            app=request.getSession().getServletContext();
            sb.append("<span style='font-size:14px;'><b>[APPLICATION]:</b></span><br />");
            sb.append("<span style='font-size:11px;'><b>Atributos</b></span><br />");
            eTmp=app.getAttributeNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(getInformation(app.getAttribute(cTmp))).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Parametros Iniciales</b></span><br />");
            eTmp=app.getInitParameterNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(app.getInitParameter(cTmp)).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Servlet Version</b></span><br />");
            sb.append(app.getMinorVersion()).append(" - ").append(app.getMajorVersion()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Ruta</b></span><br />");
            sb.append(app.getContextPath()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Server</b></span><br />");
            sb.append(app.getServerInfo()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Nombre Contexto</b></span><br />");
            sb.append(app.getServletContextName()).append("<br />");
            
            //despliega info sobre la session
            ses=request.getSession();
            sb.append("<br /><br />");
            sb.append("<span style='font-size:14px;'><b>[SESSION]:</b></span><br />");
            sb.append("<span style='font-size:11px;'><b>Atributos</b></span><br />");
            eTmp=ses.getAttributeNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(getInformation(ses.getAttribute(cTmp))).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Id</b></span><br />");
            sb.append(ses.getId()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Creacion</b></span><br />");
            sb.append(sdf.format(new Date(ses.getCreationTime()))).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Ult. Acceso</b></span><br />");
            sb.append(sdf.format(new Date(ses.getLastAccessedTime()))).append("<br />");
            
            //despliega info sobre la peticion
            sb.append("<br /><br />");
            sb.append("<span style='font-size:14px;'><b>[PETICION]:</b></span><br />");
            sb.append("<span style='font-size:11px;'><b>Atributos</b></span><br />");
            eTmp=request.getAttributeNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(getInformation(request.getAttribute(cTmp))).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Parametros</b></span><br />");
            eTmp=request.getParameterNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(request.getParameter(cTmp)).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Cabecera</b></span><br />");
            eTmp=request.getHeaderNames();
            while( eTmp.hasMoreElements() ){
                cTmp=eTmp.nextElement();
                sb.append("<b>").append(cTmp).append("</b>: ").append(request.getHeader(cTmp)).append("<br />");
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Cookies</b></span><br />");
            if( request.getCookies()!=null ){
                for(Cookie co: request.getCookies()){
                    sb.append("<b>").append(co.getName()).append("[").append(co.getPath()).append("]</b>: ").append(co.getValue()).append("<br />");
                }
            }
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Metodo</b></span><br />");
            sb.append(request.getMethod()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>Cliente</b></span><br />");
            sb.append(request.getRemoteHost()).append(" - ").append(request.getRemoteAddr()).append(" - ").append(request.getRemotePort()).append(", ").append(request.getRemoteUser()).append("<br />");
            sb.append(request.getLocalName()).append(" - ").append(request.getLocalAddr()).append(" - ").append(request.getLocalPort()).append("<br />");
            sb.append("<br />");
            sb.append("<span style='font-size:11px;'><b>URI</b></span><br />");
            sb.append(request.getRequestURI()).append("</span><br />");
        }

        return sb.toString();
    }

    private static String getInformation(Object obj){
        StringBuilder sb=new StringBuilder("");
        Iterator ite;
        int i=0;

        if(obj==null){
            return null;
        }

        try{
            if(obj instanceof Collection){
                ite=((Collection)obj).iterator();

                sb.append("{");
                while(ite.hasNext()){
                    if(i>0){
                        sb.append(", ");
                    }
                    sb.append(ite.next());
                    i++;
                }
                sb.append("}");
            }
            else if(obj instanceof Object[]){
                sb.append("[");
                for(i=0; i<((Object[])obj).length; i++){
                    if(i>0){
                        sb.append(", ");
                    }
                    sb.append(((Object[])obj)[i]);
                }
                sb.append("]");
            }
            else{
                sb.append(obj);
            }
        }
        catch(Exception ex){
            sb.append("Con error!!!");
        }

        return sb.toString();
    }

}
