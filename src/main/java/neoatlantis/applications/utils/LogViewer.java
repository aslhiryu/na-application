package neoatlantis.applications.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class LogViewer {
    private static final Logger DEBUGER=Logger.getLogger(LogViewer.class);
    
    protected File log;
    protected String error;
    protected long position;
    protected long pagAnt;
    protected long pagAct;
    
    public LogViewer(File f){
        this.log=f;
    }
    
    public LogViewer(String f){
        this.log=new File(f);
    }
    
    
    
    
    // -----------------------------------------------------
    
    public String[] readLines(int regs){
        String[] res=this.preparaResultados(regs);
        int pos=-1;
        String line;
            
        this.error=null;
        DEBUGER.debug("Intento leer el archivo: "+(this.log!=null? this.log.getAbsolutePath(): "null"));
        
        try{
            FileInputStream fis = new FileInputStream(this.log);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            DEBUGER.debug("Me posiciono en el byte: "+this.position);
            this.pagAnt=this.pagAct;
            this.pagAct=0;
            br.skip(this.position);
            while ((line = br.readLine()) != null) {
                DEBUGER.debug("Linea: "+line);
                //reviso que sea relamente el inicio de una nueva linea
                if( line.startsWith("[") ){
                    pos++;
                }
                else if( pos==-1 ){
                    continue;
                }
                
                //reviso que sean el numero necesario de registros
                if(pos>=regs){
                    break;
                }
                
                res[pos]+=line;
                this.pagAct+=line.length()+1;
                //DEBUGER.debug("Leo la linea "+pos+", con un tamaño de "+line.length());
                //DEBUGER.debug("Posicion actual: "+this.position);
            }

            br.close();
            fis.close();
            
            DEBUGER.debug("Pagina actual: "+this.pagAct);
        }
        catch(Exception ex){
            this.error=ex.getMessage();
            DEBUGER.error("Error al leer linas del log", ex);
        }
        
        return res;
    }
    
    public boolean nextPage(){
        this.position+=this.pagAct;
        
        return true;
    }
    
    public boolean previousPage(){
        this.position-=this.pagAnt;
        
        if(this.position<0){
            this.position=0;
        }
        
        return true;
    }
    
    public String existError(){
        return this.error;
    }
    
    public void reset(){
        this.position=0;
        this.error=null;
    }
    
    public boolean hasPrevious(){
        return this.position>0;
    }
    
    public boolean hasNext(){
        return this.position<this.log.length();
    }
    
    
    
    // --------------------------------------------------------------------
    
    private String[] preparaResultados(int tam){
        String[] res=new String[tam];
        
        for(int i=0; i<tam; i++){
            res[i]="";
        }
        
        return res;
    }
    
}
