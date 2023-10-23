package neoAtlantis.utilidades.apps.catalogs.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryTable {
    private String nombre;
    private String texto;
    private List<MemoryColumn> columnas=new ArrayList<MemoryColumn>();
    private List<Object[]> data=new ArrayList<Object[]>();
    private List<MemoryColumn> llaves=new ArrayList<MemoryColumn>();
    private boolean enMemoria;
    private long regs;
    
    public MemoryTable(String nombre){
        this.nombre=nombre;
        this.texto=nombre;
    }
    
    public String getNombre(){
        return this.nombre;
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
     * @return the enMemoria
     */
    public boolean isEnMemoria() {
        return enMemoria;
    }
    
    public void setRegs(long r){
        this.regs=r;
    }
    
    public long getNumeroRegistros(){
        if( this.enMemoria ){
            return this.data.size();
        }
        
        return this.regs;
    }

    /**
     * @param enMemoria the enMemoria to set
     */
    public void setEnMemoria(boolean enMemoria) {
        this.enMemoria = enMemoria;
    }

    public void agregaDato(Object[] dato){
        this.data.add(dato);
    }
    
    public void asignaDatos(List<Object[]> datos){
        this.data=datos;
    }

    public void agregaColumna(MemoryColumn col){
        boolean existe=false;
        
        for(MemoryColumn cTmp: this.columnas){
            if( cTmp.getNombre().equalsIgnoreCase(col.getNombre()) ){
                //iguala datos
                cTmp.setTamano(col.getTamano());
                cTmp.setTipo(col.getTipo());
                
                existe=true;
                break;
            }
        }
        
        if( !existe ){
            this.columnas.add(col);
        }
    }

    public List<MemoryColumn> getColumnas(){
        return this.columnas;
    }    

    public void agregaLlave(String campo){
        boolean existe=false;
        
        for(MemoryColumn cTmp: this.columnas){
            if( cTmp.getNombre().equalsIgnoreCase(campo) ){
                cTmp.setLlave(true);                
                this.llaves.add(cTmp);
                agregaColumna(cTmp);
                existe=true;
                break;
            }
        }
        
        if(!existe){
            MemoryColumn cTmp=new MemoryColumn(campo);
            cTmp.setLlave(true);                
            this.llaves.add(cTmp);
            agregaColumna(cTmp);
        }
    }
    
    public List<MemoryColumn> getLlaves(){
        return this.llaves;
    }    

    public List<Object[]> getDatos(){
        return this.data;
    }    

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("\t||--------------------------------  Catalogo  --------------------------------||\n");
        sb.append("\tNombre: ").append(this.nombre).append("\n");
        sb.append("\tTexto: ").append(this.texto).append("\n");
        sb.append("\tDatos: ").append(this.data.size()).append("\n");
        sb.append("\tEn Memoria: ").append(this.enMemoria).append("\n");
        sb.append("\tLlave: ");
        for(MemoryColumn c: this.llaves){
            sb.append(c.getNombre()).append(", ");
        }
        sb.append("\n");
        for(MemoryColumn c: this.columnas){
            sb.append(c).append("\n");
        }
        
        return sb.toString();
    }

}
