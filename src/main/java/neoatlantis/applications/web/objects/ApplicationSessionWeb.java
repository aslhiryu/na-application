package neoatlantis.applications.web.objects;

import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import neoatlantis.accesscontroller.objects.ApplicationSession;

/**
 *
 * @author hiryu
 */
public class ApplicationSessionWeb extends ApplicationSession {
    private HttpSession httpSession ;
    private UserRequest lastRequest;

    /**
     * Constructor
     * @param httpSession Sesión Web
     * @param ip IP desde donse que genero la sesión
     */
    public ApplicationSessionWeb(HttpSession httpSession, String ip){
        super(ip);
        this.httpSession=httpSession;
    }
    
    /**
     * Constructor
     * @param httpSession  Sesión Web
     */
    public ApplicationSessionWeb( HttpSession httpSession){
        this(httpSession,  "0.0.0.0");
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
    
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        sb.append("Id: ").append(this.getId()).append("\n");
        sb.append("Origen: ").append(this.getIp()).append("\n");
        sb.append("S.O.: ").append(this.getOs()).append("\n");
        sb.append("Navegador: ").append(this.getBrowser()).append("\n");
        sb.append("Creación: ").append(sdf.format(this.getCreated())).append("\n");
        sb.append("Actividad: ").append(sdf.format(this.getLastActivity())).append("\n");
        sb.append("Ult. Peticion: ").append(this.lastRequest).append("\n");

        return sb.toString();
    }


    
    @Override
    public void destroySession() {
        if( this.httpSession!=null ){
            this.httpSession.invalidate();
        }
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
