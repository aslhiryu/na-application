package neoatlantis.applications.objetcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import neoatlantis.accesscontroller.objects.EnvironmentType;
import neoatlantis.accesscontroller.objects.Role;
import neoatlantis.accesscontroller.objects.User;
import neoatlantis.applications.web.objects.ApplicationSession;

/**
 *
 * @author hiryu
 */
public class WebUser extends User {
    private ApplicationSession sesion;

    public WebUser(String id, String user, String origen, String terminal, EnvironmentType tipoTerminal) {
        super(id, user, origen, terminal, tipoTerminal, true);
    }




    public void setActivityDate(Date actividad) {
        super.setActivityDate(actividad);
        if( this.sesion!=null ){
            this.sesion.setLastActivity(new Date());
        }
    }

    /**
     * Recupera la sesion del usuario
     * @return the sesion
     */
    public ApplicationSession getSession() {
        return sesion;
    }
    
    /**
     * Define una nueva sesión pra ael usuario
     * @param session 
     */
    public void newSession(ApplicationSession session) {
        this.sesion=session;
    }
    
    /**
     * Genera la informac&oacute;n del usuario.
     * @return Informaci&oacute;n del usuario
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        String cTmp;

        sb.append("/*********************  USUARIO  ********************//").append(System.getProperty("line.separator"));
        sb.append("ID: ").append(this.getId()).append(System.getProperty("line.separator"));
        sb.append("Nombre: ").append(this.getName()).append(System.getProperty("line.separator"));
        sb.append("User: ").append(this.getUser()).append(System.getProperty("line.separator"));
        sb.append("Mail: ").append(this.getMail()).append(System.getProperty("line.separator"));
        for(int i=0; this.getProperties()!=null && i<this.getProperties().length; i++){
            sb.append(this.getProperties()[i]).append(": ");
            for (int j=0; this.getProperty(this.getProperties()[i])!=null && j<this.getProperty(this.getProperties()[i]).length; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append(this.getProperty(this.getProperties()[i])[j]);
            }
            sb.append(System.getProperty("line.separator"));
        }
        sb.append("Roles: ");
        for (int i = 0; this.getRoles() != null && i < this.getRoles().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(((Role) this.getRoles().get(i)).getName());
        }
        sb.append(System.getProperty("line.separator"));
        sb.append(super.toString());
        sb.append("Origen: ").append(this.getOrigin()).append(System.getProperty("line.separator"));
        sb.append("Terminal: ").append(this.getTerminal()).append(System.getProperty("line.separator"));
        sb.append("Tipo de Terminal: ").append(this.getEnvironmentType()).append(System.getProperty("line.separator"));
        sb.append("Estado: ").append(this.getState()).append(System.getProperty("line.separator"));
        sb.append("Sesion: ").append(this.sesion).append(System.getProperty("line.separator"));
        sb.append("Generación: ").append(this.getCreatedDate()).append(System.getProperty("line.separator"));
        sb.append("Activo: ").append(this.isActive()).append(System.getProperty("line.separator"));
        sb.append("Ult. Acceso: ").append(this.getLastAccessDate()).append(System.getProperty("line.separator"));
        sb.append("Ult. Actividad: ").append(this.getActivityDate()).append(System.getProperty("line.separator"));
        sb.append("/****************************************************//").append(System.getProperty("line.separator"));

        return sb.toString();
    }

    
    @Override
    public WebUser clone(){
        WebUser uTmp=new WebUser(this.getId(), this.getUser(), this.getOrigin(), this.getTerminal(), this.getEnvironmentType()) ;
        
        uTmp.setActive(this.isActive());
        uTmp.setActivityDate(this.getActivityDate());
        uTmp.setState(this.getState());
        uTmp.setCreatedDate(this.getCreatedDate());
        uTmp.setMail(this.getMail());
        uTmp.setName(this.getName());
        uTmp.permisos=(ArrayList)this.permisos.clone();
        uTmp.setPhoto(this.getPhoto());
        uTmp.propiedades=this.propiedades;
        uTmp.roles=(ArrayList)((ArrayList)this.roles).clone();
        uTmp.sesion=this.sesion;
        uTmp.setLastAccessDate(this.getLastAccessDate());
        
        return uTmp;
    }
}
