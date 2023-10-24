package neoatlantis.applications.web.objects;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class RequestStatistics {
    private long total=0;
    private double minimo=-1;
    private double maximo=-1;
    
    public long getTotal(){
        return this.total;
    }
    
    public double getMinimal(){
        return this.minimo;
    }
    
    public double getMaximum(){
        return this.maximo;
    }
    
    public double getAverage(){
        if(this.maximo==-1 || this.minimo==-1){
            return 0;
        }
        
        return (this.minimo+this.maximo)/2;
    }
    
    public void updateStatistics(double val){
        if( this.minimo==-1 || val<this.minimo){
            this.minimo=val;
        }
        if( val>this.maximo ){
            this.maximo=val;
        }
    }
    
    public void updateTotal(long val){
        if(val>this.total){
            this.total=val;
        }
    }
}
