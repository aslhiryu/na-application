package neoatlantis.applications.web;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neoatlantis.applications.parameters.objects.MemoryParameters;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.objects.ApplicationSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class AdminAppServlet extends HttpServlet {
    private static final Logger DEBUGER=Logger.getLogger(AdminAppServlet.class);
    
    public static final String PATH_SERVICE="/neoAtlantis/resources/web/adminApp.service";
    public static final String OPERATION_PARAM="NA_Operation";
    public static final String SESSION_PARAM="NA_Session";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String salida;
        List<ApplicationSession> sess;
        MemoryParameters mp;

        DEBUGER.debug("Se realiza una solicitud al servlet de administración de aplicación, con operación '"+request.getParameter(OPERATION_PARAM)+"' y solicitante '"+request.getParameter(SESSION_PARAM)+"'");
        
        //recupero las sesiones del sistema
        sess=(List<ApplicationSession>)request.getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        mp=(MemoryParameters)request.getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);
        if( sess!=null ){
            //revisa que sea una sesion valida
            if( existSession(sess, request.getParameter(SESSION_PARAM)) ){
                if( request.getParameter(OPERATION_PARAM)!=null && request.getParameter(OPERATION_PARAM).equalsIgnoreCase("updateParam") ){
                    if( mp!=null && request.getParameter("param")!=null && request.getParameter("value")!=null ){
                        try{
                            DEBUGER.debug("Parametros originales: "+mp.toString());
                            salida="DATA:"+mp.updateParameter(request.getParameter("param"), request.getParameter("value"));
                            DEBUGER.debug("Parametros actualizados: "+mp.toString());
                        }
                        catch(Exception ex){
                            salida="ERROR:Operacion no valida. "+ex.getMessage();
                        }
                    }
                    else if( mp!=null && request.getParameter("param")==null ){
                        salida="ERROR:No se proporciono el parametro de memoria a actualizar";
                    }
                    else if( mp!=null && request.getParameter("value")==null ){
                        salida="ERROR:No se proporciono el valor a asignar";
                    }
                    else{
                        salida="ERROR:No existen paremtros en memoria";
                    }
                }
                else{
                    salida="ERROR:Operacion no valida";
                }
            }
            else{
                salida="ERROR:La session no es valida";
            }
        }
        else{
            salida="ERROR:No existe sesiones activas";
        }
        
        response.getOutputStream().print(salida);
        response.flushBuffer();
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.doGet(request, response);
    }

    
    
    
    // metodos privados --------------------------------------------------------
    
    private boolean existSession(List<ApplicationSession> sessions, String session){
        for(int i=0; sessions!=null&&i<sessions.size(); i++){
            if( sessions.get(i).getHttpSession().getId().equals(session) ){
                return true;
            }
        }
        
        return false;
    }
}
