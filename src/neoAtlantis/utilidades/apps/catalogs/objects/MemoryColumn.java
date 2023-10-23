package neoAtlantis.utilidades.apps.catalogs.objects;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryColumn {
    private String nombre;
    private String texto;
    private TypeData tipo;
    private int tamano;
    private TypeOrder ordenacion;
    private boolean llave;
    private boolean visible=true;
    private boolean actividad;
    private boolean capturable=true;
    private  String referencia;
    private  boolean unico;
    
    public MemoryColumn(String nombre){
        this.nombre=nombre;
        this.texto=nombre;
    }
    
    public String getNombre(){
        return this.nombre;
    }

    /**
     * @return the tipo
     */
    public TypeData getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(TypeData tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the tamano
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * @param tamano the tamano to set
     */
    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * @return the llave
     */
    public boolean isLlave() {
        return llave;
    }

    /**
     * @param llave the llave to set
     */
    public void setLlave(boolean llave) {
        this.llave = llave;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the ordenacion
     */
    public TypeOrder getOrdenacion() {
        return ordenacion;
    }

    /**
     * @param ordenacion the ordenacion to set
     */
    public void setOrdenacion(TypeOrder ordenacion) {
        this.ordenacion = ordenacion;
    }

    /**
     * @return the actividad
     */
    public boolean isActividad() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActividad(boolean actividad) {
        this.actividad = actividad;
    }

    /**
     * @return the capturable
     */
    public boolean isCapturable() {
        return capturable;
    }

    /**
     * @param capturable the capturable to set
     */
    public void setCapturable(boolean capturable) {
        this.capturable = capturable;
    }
    
    public String getReferencia() {
        return this.referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isUnico() {
        return this.unico;
    }

    public void setUnico(boolean unico) {
        this.unico = unico;
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("\t\t||--------------------------------  Columna  --------------------------------||\n");
        sb.append("\t\tNombre: ").append(this.nombre).append("\n");
        sb.append("\t\tTexto: ").append(this.texto).append("\n");
        sb.append("\t\tTipo: ").append(this.tipo).append("\n");
        sb.append("\t\tTamaño: ").append(this.tamano).append("\n");
        sb.append("\t\tLlave: ").append(this.llave).append("\n");
        sb.append("\t\tVisible: ").append(this.visible).append("\n");
        sb.append("\t\tOrdenacion: ").append(this.ordenacion).append("\n");
        sb.append("\t\tActividad: ").append(this.actividad).append("\n");
        sb.append("\t\tCapturable: ").append(this.capturable).append("\n");
        sb.append("\t\tUnico: ").append(this.unico).append("\n");
        if(this.referencia!=  null){
            sb.append("\t\tLigado a : ").append(this.referencia).append("\n");
        }
        
        return sb.toString();
    }

}
