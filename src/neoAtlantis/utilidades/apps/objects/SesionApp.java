package neoAtlantis.utilidades.apps.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;

/**
 * Objeto que contiene la información de un sesion http
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class SesionApp {
    private String id;
    private String ip;
    private Date creacion ;
    private Date actividad ;
    private OperatingSystem os;
    private Browser browser;
    private HttpSession httpSession ;

    public SesionApp(HttpSession httpSession, String id, String ip){
        this.id=id;
        this.ip=ip;
        this.creacion=new Date();
        this.actividad=new Date();
        this.httpSession =httpSession;
    }
    
    public SesionApp( HttpSession httpSession, String id){
        this(httpSession, id, "0.0.0.0");
    }
    
    public SesionApp(String id, String ip) {
        this.id = id;
        this.ip = ip;
        this.creacion = new Date();
        this.actividad = new Date();
    }
    
    public SesionApp(String id) {
        this(id, "0.0.0.0");
    }
    
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sb.append("Id: ").append(this.id).append("\n");
        sb.append("Origen: ").append(this.ip).append("\n");
        sb.append("S.O.: ").append(this.os).append("\n");
        sb.append("Navegador: ").append(this.browser).append("\n");
        sb.append("Creación: ").append(sdf.format(this.getCreacion())).append("\n");
        sb.append("Actividad: ").append(sdf.format(this.getActividad())).append("\n");

        return sb.toString();
    }

    public Date getCreacion() {
        return this.creacion;
    }

    public Date getActividad() {
        return this.actividad;
    }

    public void setActividad(Date evento) {
        this.actividad = evento;
    }

    /**
     * @return the os
     */
    public OperatingSystem getOs() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    /**
     * @return the browser
     */
    public Browser getBrowser() {
        return browser;
    }

    /**
     * @param browser the browser to set
     */
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public HttpSession getHttpSession() {
        return this.httpSession;
    }
}
