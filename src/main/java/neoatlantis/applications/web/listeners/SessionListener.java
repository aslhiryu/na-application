package neoatlantis.applications.web.listeners;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import neoatlantis.applications.web.objects.ApplicationSession;
import neoatlantis.applications.web.objects.Browser;
import neoatlantis.applications.web.objects.OperatingSystem;
import neoatlantis.applications.web.objects.RequestStatistics;
import neoatlantis.applications.web.objects.UserRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class SessionListener implements HttpSessionListener {
    private static final Logger DEBUGGER=Logger.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        //recupero las sesiones activas
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)hse.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        boolean existe=false;
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(hse.getSession().getId()) ){
                existe=true;
                break;
            }
        }
        if( !existe ){
            sesTmp.add(new ApplicationSession(hse.getSession()));
            DEBUGGER.debug("Se genera la sesión "+hse.getSession().getId()+".");
        }
        DEBUGGER.debug("Se genera una nueva sesion, existen "+sesTmp.size()+" sesiones.");
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {        
        List<ApplicationSession> sesTmp=Collections.synchronizedList( (List<ApplicationSession>)hse.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY) );
        
        removeSession(sesTmp, hse.getSession().getId());
        validateSession(sesTmp,hse.getSession().getMaxInactiveInterval());
        
                //actualiza los datos de la estadistica
                RequestStatistics sta=(RequestStatistics)hse.getSession().getServletContext().getAttribute(ApplicationListener.SESSION_STATISTICS_KEY);
                if(sta!=null){
                    double t=((double)((new Date()).getTime()- hse.getSession().getCreationTime())/1000/60);
                    sta.updateStatistics(t);
                }
        
        DEBUGGER.debug("Se elimina una sesion, quedan "+sesTmp.size()+" sesiones.");
    }

    // MLS----------------------------------------------------------------------
    
    /**
     * Revisa se la sesiones aun continuan activas
     * @param lis Lista de sesiones
     * @param dur Duración de la sesión
     */
    public static void validateSession(List<ApplicationSession> lis, long dur) {
        DEBUGGER.debug("Revisa el vencimiento de las sesiones ("+dur+" segs).");        
        ApplicationSession s;
        
        synchronized(lis){
            for(int i=0; lis!=null&&i<lis.size(); i++){
                s=lis.get(i);

                if( ((1000*dur)+s.getLastActivity().getTime())<(new Date()).getTime() ) {
                    if( s.getHttpSession()!=null ){
                        try{
                            s.getHttpSession().invalidate();
                            DEBUGGER.debug("Forzo la desctrucción de la sesión web '"+s.getId()+"\'.");
                        }catch(Exception ex){}
                    }

                    removeSession(lis, s.getId());
                     DEBUGGER.debug("Forzo la desctrucción de la sesión NA '"+s.getId()+"\'.");
                }
            }
        }
    }
    
    /**
     * Metodo que almacena la ultima peticion web realizada
     * @param req  Peticion Web
     */
    public static void saveLastRequest(HttpServletRequest req){
        DEBUGGER.debug("Guarda la ultima peticion realizada en la sesion");
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)req.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(req.getSession().getId()) ){
                s.setLastRequest( ApplicationSession.parseRequest(req) );
                DEBUGGER.debug("Asigno la ultima peticion realizada: "+s.getLastRequest());
                break;
            }
        }
    }
    
    /**
     * Metodo que limpia la ultima peticion web realizada
     * @param req Peticion Web
     */
    public static void clearLastRequest(HttpServletRequest req){
        DEBUGGER.debug("Guarda la ultima peticion realizada en la sesion");
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)req.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(req.getSession().getId()) ){
                s.setLastRequest(null);
                DEBUGGER.debug("Limpia la ultima peticion realizada.");
                break;
            }
        }
    }
    
    /**
     * Metodo que recupera la ultima peticion del usuario que se haya realizado
     * @param req Peticion Web
     * @return La ultima peticion realizada
     */
    public static UserRequest getLastRequest(HttpServletRequest req){
        DEBUGGER.debug("Recupera la ultima peticion realizada en la sesion");
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)req.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(req.getSession().getId()) ){
                return s.getLastRequest();
            }
        }
        
        return null;
    }

    /**
     * Asigna la IP del cliente a un sesion en especifico
     * @param req Peticion web
     */
    public static void setIP(HttpServletRequest req){
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)req.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        DEBUGGER.debug("Intento asignar IP.");
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(req.getSession().getId()) && s.getIp().equals("0.0.0.0") ){
                s.setIp(req.getRemoteAddr());
                DEBUGGER.debug("Se asigna la IP "+s.getIp()+" a la sesión "+s.getId()+".");
                break;
            }
        }
    }

    /**
     * Asigna el SO del cliente a un sesion en especifico
     * @param req Petición del cliente
     */
    public static void setOS(HttpServletRequest req){
        List<ApplicationSession> sesTmp=(List<ApplicationSession>)req.getSession().getServletContext().getAttribute(ApplicationListener.SESSIONS_KEY);
        
        for(ApplicationSession s: sesTmp){
            if( s.getHttpSession().getId().equals(req.getSession().getId()) && s.getBrowser()==null ){
                DEBUGGER.debug("User-Agent: "+req.getHeader("user-agent")+".");
                
                s.setOs(getOS(req.getHeader("user-agent")));
                s.setBrowser(getBrowser(req.getHeader("user-agent")));
                DEBUGGER.debug("El sistema operativo es  "+s.getOs()+" en la sesión "+s.getHttpSession().getId()+".");
                DEBUGGER.debug("El navegador es  "+s.getBrowser()+" en la sesión "+s.getHttpSession().getId()+".");
                break;
            }
        }
    }
    
    /**
     * Recupera el tipo de SO del cliente a partir de su cabecera de llamada
     * @param cabecera Cabecera HTTP de llamada
     * @return 
     */
    private static OperatingSystem getOS(String cabecera){
        if( cabecera!=null && cabecera.toLowerCase().indexOf("windows ce")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows phone")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows mobile")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows")!=-1 ){
            return OperatingSystem.WINDOWS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("linux")!=-1 ){
            return OperatingSystem.LINUX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("iphone")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("ipod")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("ipad")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("mac os")!=-1 ){
            return OperatingSystem.MAC;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("android")!=-1 ){
            return OperatingSystem.ANDROID;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("hpux")!=-1 ){
            return OperatingSystem.HP_UX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("sunos")!=-1 ){
            return OperatingSystem.SOLARIS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("playbook")!=-1 ){
            return OperatingSystem.PLAYBOOK;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("blackberry")!=-1 ){
            return OperatingSystem.BLACKBERRY;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("pingdom")!=-1 ){
            return OperatingSystem.MONITORING;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("microsoft office")!=-1 ){
            return OperatingSystem.MS_OFFICE;
        }
        
        DEBUGGER.error("No se logro definir el sistema operativo para la cabecera: "+cabecera);
        return OperatingSystem.OTRO;
    }

    /**
     * Recupera el tipo de Navegador del cliente a partir de su cabecera de llamada
     * @param cabecera Cabecera HTTP de llamada
     * @return 
     */
    private static Browser getBrowser(String cabecera){
        if( cabecera!=null && cabecera.toLowerCase().indexOf("firefox")!=-1 ){
            return Browser.FIREFOX;
        }
        if( cabecera!=null && cabecera.toLowerCase().indexOf("mozilla")!=-1 ){
            return Browser.FIREFOX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("safari")!=-1 ){
            return Browser.SAFARI;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("opera")!=-1 ){
            return Browser.OPERA;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("netscape")!=-1 ){
            return Browser.OPERA;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("msie")!=-1 ){
            return Browser.INTERNET_EXPLORER;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("chrome")!=-1 ){
            return Browser.GOOGLE_CHROME;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("pingdom")!=-1 ){
            return Browser.MONITORING;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("microsoft office")!=-1 ){
            return Browser.MICROSOFT_OFFICE;
        }
        
        DEBUGGER.error("No se logro definir el navegador para la cabecera: "+cabecera);
        return Browser.OTRO;
    }
    
    // MP-----------------------------------------------------------------------

    private static void removeSession(List<ApplicationSession> lis, String ses){
        DEBUGGER.debug("Sesion a eliminar '"+ses+"'.");

        for(ApplicationSession s: lis){
            if( s.getId().equals(ses)   || (s.getHttpSession()!=null && s.getHttpSession().getId().equals(ses)) ){
                lis.remove(s);
                
                DEBUGGER.debug("Se elimina una sesión, quedan "+lis.size()+" sesiones.");
                break;
            }
        }
    }
    
}
