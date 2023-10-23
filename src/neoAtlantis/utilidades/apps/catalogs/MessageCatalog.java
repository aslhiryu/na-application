package neoAtlantis.utilidades.apps.catalogs;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MessageCatalog {
    private String mensaje;
    private MessageType tipo;

    public MessageCatalog(MessageType tipo, String mensaje){
        this.tipo=tipo;
        this.mensaje=mensaje;
    }
    
    public MessageCatalog(String mensaje){
        this(MessageType.ERROR, mensaje);
    }
    
    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the tipo
     */
    public MessageType getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(MessageType tipo) {
        this.tipo = tipo;
    }
    
    
}
