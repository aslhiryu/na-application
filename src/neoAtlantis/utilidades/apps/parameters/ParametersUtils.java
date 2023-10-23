package neoAtlantis.utilidades.apps.parameters;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ParametersUtils {
    
    public static boolean getBooleanValue(String valor){
        boolean b=false;
        
        if(valor!=null && (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("t") || valor.equalsIgnoreCase("yes") ||
                valor.equalsIgnoreCase("y") || valor.equalsIgnoreCase("si") || valor.equalsIgnoreCase("s") ||
                valor.equals("1"))){
            b=true;
        }
        
        return b;
    }
}
