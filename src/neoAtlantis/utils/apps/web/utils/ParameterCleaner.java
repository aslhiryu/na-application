package neoAtlantis.utils.apps.web.utils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public final class ParameterCleaner {
    private static final Logger DEBUGER=Logger.getLogger(ParameterCleaner.class);
    
    private ParameterCleaner(){        
    }
    
    public static Map<String,String[]> cleanParameter(final Map<String,String[]> params){
        String[] vTmp;
        Map<String,String[]> pTmp=new HashMap<String,String[]>();
        
        if(params!=null){
            //recorro los parametros y elimino las posibles tags
            for(String llave: params.keySet()){
                //valido que tenga valor
                if( params.get(llave)!=null ){
                    //inicio mi variable temporal
                    vTmp=new String[params.get(llave).length];
                    
                    //recorro los valores
                    for(int i=0; i<params.get(llave).length; i++){
                        vTmp[i]=params.get(llave)[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    }
                    
                    //asigno el nuevo valor
                    pTmp.put(llave, vTmp);
                }
            }
        }
        
        return pTmp;
    }
    
    public static String getRelativeURL(HttpServletRequest request){
        return getRelativeURL(request.getRequestURL().toString(), request.getContextPath());
    }
    
    public static String getRelativeURL(String url, String contextPath){
        if( url!=null && contextPath!=null){
            String cTmp=url.substring(url.indexOf(contextPath+"/")+contextPath.length()+1);
            
            if(cTmp.indexOf('?')>1){
                cTmp=cTmp.substring(0, cTmp.indexOf('?'));
            }
            
            DEBUGER.debug("URL determinado: "+cTmp);
            
            return cTmp;
        }
        else{
            return "/";
        }
    }
    
    public static String getRelativeExtension(HttpServletRequest request){
        return getRelativeExtension(request.getRequestURL().toString());
    }
    
    public static String getRelativeExtension(String url){
        int fin=0;
        String ext=null;
        
        if( url!=null ){
            fin=url.lastIndexOf('?');
            
            //reviso que no sea un directorio
            if( url.lastIndexOf('.')>0 ){
                if( fin>0 ){
                    ext=url.substring(url.lastIndexOf('.')+1, fin);
                }
                else{
                    ext=url.substring(url.lastIndexOf('.')+1);
                }
            }
        }
        
        return ext;
    }
    
    public static boolean isPage(String extension){
        if( extension==null ){
            return false;
        }
        else if( extension.equalsIgnoreCase("HTML") || extension.equalsIgnoreCase("HTM")
                || extension.equalsIgnoreCase("JSP") || extension.equalsIgnoreCase("JSF")
                || extension.equalsIgnoreCase("DO") || extension.equalsIgnoreCase("ACTION") ){
            return true;
        }
        
        return false;
    }
}
