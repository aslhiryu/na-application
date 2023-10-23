package neoAtlantis.utilidades.apps.escuchadores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import neoAtlantis.utilidades.apps.objects.SesionApp;
import org.apache.log4j.Logger;

/**
 * Clase que prepara la sesión del aplicativo
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class SesionListener implements HttpSessionListener {
    static Logger debugger=Logger.getLogger(SesionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)hse.getSession().getServletContext().getAttribute(AppListener.APP_SESIONES);
        boolean existe=false;
        
        for(SesionApp s: sesTmp){
            if( s.getId().equals(hse.getSession().getId()) ){
                existe=true;
                break;
            }
        }
        if( !existe ){
            sesTmp.add(new SesionApp(hse.getSession().getId()));
            debugger.debug("Se genera la sesión "+hse.getSession().getId()+".");
        }
        debugger.debug("Se genera una nueva sesion, existen "+sesTmp.size()+" sesiones.");
        
        //return sesTmp.size();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)hse.getSession().getServletContext().getAttribute(AppListener.APP_SESIONES);
        
        this.remueveSesion(sesTmp, hse.getSession().getId());
        validaSesionesVencidas(sesTmp,hse.getSession().getMaxInactiveInterval());
        
        debugger.debug("Se elimina una sesion, quedan "+sesTmp.size()+" sesiones.");
    }
    
    public static void validaSesionesVencidas(List<SesionApp> lis, long dur) {
        SesionApp s;

        debugger.debug("Revisa el vencimiento de las sesiones ("+dur+" segs.");
        for (int i = 0; lis != null && i < lis.size(); i++) {
            s=lis.get(i);
            if( ((1000*dur)+s.getActividad().getTime())<(new Date()).getTime() && s.getHttpSession()!=null ) {
                s.getHttpSession().invalidate();
                debugger.debug("Forzo la desctrucci\u00f3n de la sesión '"+s.getId()+"\'.");
            }
        }
    }

    //--------------------------------------------------------------------------
    
    private synchronized void remueveSesion(ArrayList<SesionApp> lis, String ses){
        debugger.debug("Sesion a eliminar '"+ses+"'.");

        
        for(SesionApp s: lis){
            if( s.getId().equals(ses) ){
                lis.remove(s);
                
                debugger.debug("Se elimina una sesion, quedan "+lis.size()+" sesiones.");
                break;
            }
        }
    }

}
