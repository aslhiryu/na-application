package neoatlantis.applications.web.objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Entidad que representa una sesión en la aplicación
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ApplicationSession implements Serializable{
    private String id;
    private String ip;
    private Date created;
    private Date lastActivity;
    private OperatingSystem os;
    private Browser browser;
    private HttpSession httpSession ;
    private UserRequest lastRequest;
    
    /**
     * Constructor
     * @param httpSession Sesión Web
     * @param ip IP desde donse que genero la sesión
     */
    public ApplicationSession(HttpSession httpSession, String ip){
        this.id=UUID.randomUUID().toString();
        this.ip=ip;
        this.created=new Date();
        this.lastActivity=new Date();
        this.httpSession =httpSession;
    }
    
    /**
     * Constructor
     * @param httpSession  Sesión Web
     */
    public ApplicationSession( HttpSession httpSession){
        this(httpSession,  "0.0.0.0");
    }
    
    /**
     * Constructor
     * @param ip IP desde donse que genero la sesión
     */
    public ApplicationSession(String ip) {
        this.ip = ip;
        this.created = new Date();
        this.lastActivity = new Date();
    }
    
    // -------------------------------------------------------------------------

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sb.append("Id: ").append(this.id).append("\n");
        sb.append("Origen: ").append(this.ip).append("\n");
        sb.append("S.O.: ").append(this.os).append("\n");
        sb.append("Navegador: ").append(this.browser).append("\n");
        sb.append("Creación: ").append(sdf.format(this.getCreated())).append("\n");
        sb.append("Actividad: ").append(sdf.format(this.getLastActivity())).append("\n");
        sb.append("Ult. Peticion: ").append(this.lastRequest).append("\n");

        return sb.toString();
    }
    
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

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the lastActivity
     */
    public Date getLastActivity() {
        return lastActivity;
    }

    /**
     * @param lastActivity the lastActivity to set
     */
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
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

    /**
     * @return the httpSession
     */
    public HttpSession getHttpSession() {
        return httpSession;
    }

    /**
     * @param httpSession the httpSession to set
     */
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    /**
     * @return the lastRequest
     */
    public UserRequest getLastRequest() {
        return lastRequest;
    }

    /**
     * @param lastRequest the lastRequest to set
     */
    public void setLastRequest(UserRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    
    
    
    
    /**
     * Metodo que genera un UserRequest a aprtir de una peticion Web
     * @param request Peticion Web
     * @return 
     */
    public static final UserRequest parseRequest(HttpServletRequest request){
        UserRequest r=new UserRequest();
        
        r.setUrl( request.getRequestURI() );
        if(request.getMethod().equalsIgnoreCase("post")){
            r.setPost(true);
        }
        
        r.setParams(request.getParameterMap());
        
        return r;
    }
}
