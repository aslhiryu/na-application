package neoAtlantis.utils.apps.parameters.objects;

/**
 * Enumeración que define representa un tipo de parametro en memoria
 * @author Hiryu (aslhiryu@gmail.com)
 */
public enum ParameterType {
    NUMERIC("N"),
    STRING("C"),
    DATE("F"),
    ENCRYPTION("E"),
    BOOLEAN("B");
    
    String value;
    ParameterType(String s) {
        value = s;
    }
    
    public static final ParameterType parse(String s){
        if( s!=null && s.equalsIgnoreCase("N") ){
            return NUMERIC;
        }
        else if( s!=null && s.equalsIgnoreCase("C") ){
            return STRING;
        }
        else if( s!=null && s.equalsIgnoreCase("F") ){
            return DATE;
        }
        else if( s!=null && s.equalsIgnoreCase("E") ){
            return ENCRYPTION;
        }
        else if( s!=null && s.equalsIgnoreCase("B") ){
            return BOOLEAN;
        }
        
        return null;
    }
    
    public String getValue(){
        return this.value;
    }
}