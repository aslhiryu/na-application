package neoAtlantis.utils.apps.catalogs.objetcs;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryColumn {
    private String id;
    private String nombre;
    private String texto;
    private DataType tipo;
    private int tamano;
    private OrderType ordenacion;
    private boolean llave;
    private boolean visible=true;
    private boolean actividad;
    private boolean capturable=true;
    private  String referencia;
    private  boolean unico;
    private DisplayType presentacion;
    
    public MemoryColumn(String nombre){
        this.id="NA:"+nombre;
        this.nombre=nombre;
        this.texto=nombre;
        this.capturable=true;
        this.tipo=DataType.CHARACTER;
        this.ordenacion=OrderType.ASC;
        presentacion=DisplayType.NORMAL;
    }
    
    public String getName(){
        return this.nombre;
    }

    /**
     * @return the tipo
     */
    public DataType getType() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setType(DataType tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the tamano
     */
    public int getSize() {
        return tamano;
    }

    /**
     * @param tamano the tamano to set
     */
    public void setSize(int tamano) {
        this.tamano = tamano;
    }

    /**
     * @return the texto
     */
    public String getText() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setText(String texto) {
        this.texto = texto;
    }

    /**
     * @return the llave
     */
    public boolean isKey() {
        return llave;
    }

    /**
     * @param llave the llave to set
     */
    public void setKey(boolean llave) {
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
    public OrderType getOrder() {
        return ordenacion;
    }

    /**
     * @param ordenacion the ordenacion to set
     */
    public void setOrder(OrderType ordenacion) {
        this.ordenacion = ordenacion;
    }

    /**
     * @return the actividad
     */
    public boolean isActivity() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActivity(boolean actividad) {
        this.actividad = actividad;
    }

    /**
     * @return the capturable
     */
    public boolean isCapture() {
        return capturable;
    }

    /**
     * @param capturable the capturable to set
     */
    public void setCapture(boolean capturable) {
        this.capturable = capturable;
    }
    
    public String getReference() {
        return this.referencia;
    }

    public void setReference(String referencia) {
        this.referencia = referencia;
    }

    public boolean isUnique() {
        return this.unico;
    }

    public void setUnique(boolean unico) {
        this.unico = unico;
    }

    public DisplayType getDisplay() {
        return this.presentacion;
    }

    public void setDisplay(DisplayType display) {
        this.presentacion = display;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("\t\t||--------------------------------  Columna  --------------------------------||\n");
        sb.append("\t\tNombre: ").append(this.nombre).append("\n");
        sb.append("\t\tId: ").append(this.id).append("\n");
        sb.append("\t\tTexto: ").append(this.texto).append("\n");
        sb.append("\t\tTipo: ").append(this.tipo).append("\n");
        sb.append("\t\tTamaño: ").append(this.tamano).append("\n");
        sb.append("\t\tLlave: ").append(this.llave).append("\n");
        sb.append("\t\tVisible: ").append(this.visible).append("\n");
        sb.append("\t\tOrdenacion: ").append(this.ordenacion).append("\n");
        sb.append("\t\tActividad: ").append(this.actividad).append("\n");
        sb.append("\t\tCapturable: ").append(this.capturable).append("\n");
        sb.append("\t\tUnico: ").append(this.unico).append("\n");
        sb.append("\t\tPresentación: ").append(this.presentacion).append("\n");
        if(this.referencia!=  null){
            sb.append("\t\tLigado a : ").append(this.referencia).append("\n");
        }
        
        return sb.toString();
    }

}
