package neoAtlantis.utils.apps.web.objects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import neoAtlantis.utilidades.entity.SimpleEntity;
import neoAtlantis.utilidades.entity.objects.OrderMode;
import neoAtlantis.utils.apps.web.listeners.PageListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class NavigationalState implements Serializable{
    private static final Logger DEBUGGER=Logger.getLogger(NavigationalState.class);
        
    private int actualPage;
    private int registries;
    private int pages;
    private String[] order;
    private boolean descedent=true;
    private Map<String, String> filters;
    private int sizePage;
    
    public NavigationalState(){
        this(10);
    }
    
    public NavigationalState(int size){
            this.order = new String[] {"1"};
            this.filters =new HashMap<String, String> ();
            this.registries = 0;
            this.pages = 0;
            this.actualPage=1;
            this.descedent=true;
            this.sizePage=size;
        }

    /**
     * @return the actualPage
     */
    public int getActualPage() {
        return actualPage;
    }

    /**
     * @param actualPage the actualPage to set
     */
    public void setActualPage(int actualPage) {
        if(  actualPage<1){
            this.actualPage=1;
        }
        else{
            this.actualPage = actualPage;
        }
    }

    /**
     * @return the registries
     */
    public int getRegistries() {
        return registries;
    }

    /**
     * @param registries the registries to set
     */
    public void setRegistries(int registries) {
        this.registries = registries;
        
        //actualiza las paginas
        this.pages=calculatePages(registries, this.sizePage);
    }

    public int getPageSize(){
        return this.sizePage;
    }
    
    /**
     * @return the pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * @return the order
     */
    public String[] getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String[] order) {
        this.order = order;
    }

    /**
     * @return the descedent
     */
    public boolean isDescedent() {
        return descedent;
    }

    /**
     * @param descedent the descedent to set
     */
    public void setDescedent(boolean descedent) {
        this.descedent = descedent;
    }

    /**
     * @return the filtres
     */
    public Map<String, String> getFilters() {
        return filters;
    }

       
        
        
        
        
        
        
        
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder("");
            Set<String> keys = this.filters.keySet();

            sb.append("============= Edo Navegacion =============").append("\n")
                .append("Pagina actual: ").append(this.actualPage).append("\n")
                .append("Registros: ").append(this.registries).append("\n")
                .append("Paginas: ").append(this.pages).append("\n")
                .append("Ordenamiento: ").append(this.order!=null? Arrays.toString(this.order): "").append("\n")
                .append("Descendente: ").append(this.descedent).append("\n")
                .append("Filtros: ").append("\n");
            for(String key: keys){
                sb.append("\t =>").append(key).append(":").append(this.filters.get(key)).append("\n");
            }
            sb.append("============================================").append("\n");

            return sb.toString();
        }
    
        public String getStringValueFilter(String filtro)
        {
            Object res = this.filters.get(filtro);

            return (res!=null? res.toString(): "");
        }

        public boolean validateValueFilter(String filtro, String value){
            if (this.filters.containsKey(filtro))
            {
                if (this.filters.get(filtro).equalsIgnoreCase(value) )
                {
                    return true;
                }
            }

            return false;
        }



        public static void startNavigationalState(HttpServletRequest request, SimpleEntity entidad){
            int p=1;
            Boolean d=null;
            
            try{
                p=Integer.parseInt( request.getParameter(PageListener.PAGE_PARAM) );
            }
            catch(Exception ex){}
            try{
                if( request.getParameter(PageListener.DESCENDING_PARAM)!=null && !request.getParameter(PageListener.DESCENDING_PARAM).isEmpty() ){
                    d=Boolean.valueOf( request.getParameter(PageListener.DESCENDING_PARAM) );
                }
            }
            catch(Exception ex){}
            
            startNavigationalState(request.getSession(),  entidad, request.getParameter(PageListener.ORDER_PARAM), p, d, request.getParameterValues(PageListener.FILTER_NAMES_PARAM), request.getParameterValues(PageListener.FILTER_VALUES_PARAM));
        }

        public static void startNavigationalState(HttpSession sesion, SimpleEntity entidad, String order, Integer pagina, Boolean desc, String[] filtros, String[] valores){
            NavigationalState edo = (NavigationalState)sesion.getAttribute(PageListener.STATE_KEY);
            String[] orderOriginal;
            boolean cambio = false;

            DEBUGGER.debug("Estado actual: "+edo);
            DEBUGGER.debug("Prepara la busqueda de registros con: ordenacion=" + order + " pagina=" + pagina + " desc=" + desc + " filtros=" + (filtros != null ? Arrays.toString( filtros) : "NULL") + " valores=" + (valores != null ? Arrays.toString( valores) : "NULL"));

            //si no existe el estado lo genero
            if (edo == null){
                edo = new NavigationalState();
            }
            orderOriginal = edo.getOrder();
            DEBUGGER.debug("Ordenamiento actual " + Arrays.toString( orderOriginal));

            //asigno valores al estado
            if (order != null){
                try{
                    edo.setOrder(  order.split(",") );
                }
                catch (Exception ex){ }
            }
            if (pagina!=null){
                edo.setActualPage(pagina);
            }

            //actualizo los datos de los filtros
            for (int i = 0; filtros != null && i < filtros.length; i++){
                try{
                    if (edo.getFilters().containsKey(filtros[i]) && isNullValue(valores[i].trim())){
                        DEBUGGER.debug("Remuevo filtro: "+filtros[i]);
                        edo.getFilters().remove(filtros[i]);
                        cambio = true;
                    }
                    else if (edo.getFilters().containsKey(filtros[i])){
                        DEBUGGER.debug("Actualizo filtro: " + filtros[i]+" de '"+edo.getFilters().get(filtros[i])+"' a '"+valores[i]+"'");
                        if( !edo.getFilters().get(filtros[i]).trim().equalsIgnoreCase(valores[i].trim()) ){
                            cambio = true;
                        }
                        edo.getFilters().put(filtros[i], valores[i].trim());
                    }
                    else if (!isNullValue(valores[i].trim())){
                        DEBUGGER.debug("Agrego filtro: " + filtros[i]);
                        edo.getFilters().put(filtros[i], valores[i].trim());
                        cambio = true;
                    }
                }
                catch(Exception ex) { }
            }
            
            //aplico los filtros a la entidad            
            for(String fil: edo.getFilters().keySet()){
                try{
                    DEBUGGER.debug("Procesando el filtro : "+fil);                    
                    entidad.setPropertyStringValue(fil, edo.getFilters().get(fil));
                }
                catch(Exception ex) { }
            }

            //cargo la configuracion del filtro
            //valido que el cambio de ordenamiento se haya realizado sobre la misma columna
            if( desc!=null ){
                if (orderOriginal[0] .equalsIgnoreCase( edo.getOrder()[0])){
                    if (desc){
                        edo.setDescedent( true);
                    }
                    else{
                        edo.setDescedent( false);
                    }
                }
                //es otra columna
                else{
                    edo.setDescedent( false);
                }
            }
            //aplica el ordenamiento en la entidad
            entidad.getParamSQL().setOrder(edo.getOrder());
            if (edo.isDescedent()){
                entidad.getParamSQL().setOrderMode(OrderMode.DESC);
            }
            else{
                entidad.getParamSQL().setOrderMode(OrderMode.ASC);
            }

            //marco la entidad como filtro
            entidad.getParamSQL().setFilter(true);

            //valido si existio un cambio en los filtros
            if (cambio){
                DEBUGGER.debug("Existe un cambio, direcciono a la primera página");
                edo.setActualPage(1);
            }

            //actualizo el estado en la sesion
            sesion.setAttribute(PageListener.STATE_KEY, edo);
        }

        public static int calculatePages(int regs, int pageSize){
            if(regs==0){
                return 0;
            }

            int p=regs/pageSize;

            if(regs%pageSize>0){
                p++;
            }

            return p;
        }
        
        public static boolean isNullValue(String valor)
        {
            if (valor != null && (valor.equals("") || valor.equals("0") || valor.equals("") || valor.equalsIgnoreCase("null")))
            {
                return true;
            }

            return false;
        }
 
}
