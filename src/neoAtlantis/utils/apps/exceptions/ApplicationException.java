package neoAtlantis.utils.apps.exceptions;

/**
 * Objeto que representa un error general de aplicacion
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ApplicationException extends Exception {
    public static final int UNKNOW=0;
    public static final int DB_CONNECTION=1000;
    public static final int DB_QUERY=1001;
    public static final int WS_CONECTION=1002;    
    public static final int APP_CONFIGURATION=1003;
    public static final int INVALID_FILE=1004;
    public static final int READ_TROUBLE=1005;
    public static final int WRITE_TROUBLE=1006;
    public static final int INVALID_DATA=1007;
    public static final int INVALID_OPERATION=1008;
    public static final int BUSSINESS_RULE=2000;
    
    private int code=UNKNOW;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    public ApplicationException(int code, String mensaje, Exception causa){
        super(mensaje, causa);
        this.code=code;
    }

    public ApplicationException(int code, String mensaje){
        super(mensaje);
        this.code=code;
    }
}
