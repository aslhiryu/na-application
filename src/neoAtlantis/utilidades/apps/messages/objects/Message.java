package neoAtlantis.utilidades.apps.messages.objects;

import java.util.Date;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class Message {

    private int id;
    private String titulo;
    private String mensaje;
    private Date creacion;
    private Date lectura;
    private String remitente;
    private String destinatario;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getCreacion() {
        return this.creacion;
    }

    public void setCreacion(Date creacion) {
        this.creacion = creacion;
    }

    public Date getLectura() {
        return this.lectura;
    }

    public void setLectura(Date lectura) {
        this.lectura = lectura;
    }

    public String getRemitente() {
        return this.remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return this.destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
