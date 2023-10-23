package neoAtlantis.utils.apps.catalogs.objetcs;

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
    
    public String getName(){
        return this.nombre;
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
     * @return the enMemoria
     */
    public boolean isInMemory() {
        return enMemoria;
    }
    
    public void setRegs(long r){
        this.regs=r;
    }
    
    public long getSizeRecords(){
        if( this.enMemoria ){
            return this.data.size();
        }
        
        return this.regs;
    }

    /**
     * @param enMemoria the enMemoria to set
     */
    public void setInMemory(boolean enMemoria) {
        this.enMemoria = enMemoria;
    }

    public void addData(Object[] dato){
        this.data.add(dato);
    }
    
    public void setData(List<Object[]> datos){
        this.data=datos;
    }

    public void addColumn(MemoryColumn col){
        boolean existe=false;
        
        for(MemoryColumn cTmp: this.columnas){
            if( cTmp.getName().equalsIgnoreCase(col.getName()) ){
                //iguala datos
                cTmp.setSize(col.getSize());
                cTmp.setType(col.getType());
                
                existe=true;
                break;
            }
        }
        
        if( !existe ){
            this.columnas.add(col);
        }
    }

    public List<MemoryColumn> getColumns(){
        return this.columnas;
    }    

    public void addKey(String campo){
        boolean existe=false;
        
        for(MemoryColumn cTmp: this.columnas){
            if( cTmp.getName().equalsIgnoreCase(campo) ){
                cTmp.setKey(true);                
                this.llaves.add(cTmp);
                addColumn(cTmp);
                existe=true;
                break;
            }
        }
        
        if(!existe){
            MemoryColumn cTmp=new MemoryColumn(campo);
            cTmp.setKey(true);                
            this.llaves.add(cTmp);
            addColumn(cTmp);
        }
    }
    
    public List<MemoryColumn> getKeys(){
        return this.llaves;
    }    

    public List<Object[]> getData(){
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
            sb.append(c.getName()).append(", ");
        }
        sb.append("\n");
        for(MemoryColumn c: this.columnas){
            sb.append(c).append("\n");
        }
        
        return sb.toString();
    }

}
