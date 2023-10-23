package neoAtlantis.utilidades.apps.escuchadores;

import java.util.Date;
import java.util.List;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utilidades.apps.objects.SesionApp;
import org.apache.log4j.Logger;

/**
 * Clase que pepara las paginas a desplegar
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PaginaListener implements ServletRequestListener {
    static Logger debugger=Logger.getLogger(SesionListener.class);

    public static final String APP_TIEMPO_CARGA="neoAtlantis.app.pagina.tiempo";

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        AppListener.asignaIpSesion(((HttpServletRequest)sre.getServletRequest()).getSession(), sre.getServletRequest().getRemoteAddr());
        AppListener.asignaSoSesion((HttpServletRequest)sre.getServletRequest());
        sre.getServletRequest().setAttribute(APP_TIEMPO_CARGA, (new Date()).getTime());
        actualizaEstado((List<SesionApp>)sre.getServletContext().getAttribute(AppListener.APP_SESIONES), ((HttpServletRequest)sre.getServletRequest()).getSession().getId());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        Long ini=(Long)sre.getServletRequest().getAttribute(APP_TIEMPO_CARGA);
        
        if( ini!=null ){
            double t=((double)((new Date()).getTime()-ini)/1000);
            debugger.info("Tiempo de generación de la pagina '"+((HttpServletRequest)sre.getServletRequest()).getServletPath()+"': "+t+" segs");
        }
        if(sre.getServletRequest().getServletContext().getAttribute(AppListener.APP_SESIONES)!=  null){
            SesionListener.validaSesionesVencidas((List)sre.getServletRequest().getServletContext().getAttribute(AppListener.APP_SESIONES),((HttpServletRequest)sre.getServletRequest()).getSession().getMaxInactiveInterval());
        }
    }

    public void actualizaEstado(List<SesionApp> sesiones, String sesion) {
        for(int i=0; sesiones!=null && i<sesiones.size(); i++){
            if( sesiones.get(i).getId().equals(sesion)!=false ) {
                sesiones.get(i).setActividad(new Date());
                debugger.info("Actualizo la actividad de la sesion '"+sesion+"'.");
            }
        }
    }
}
