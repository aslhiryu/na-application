package neoAtlantis.utils.apps.web.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;
import neoAtlantis.utils.apps.web.CaptchaImageServlet;
import org.apache.log4j.Logger;

/**
 * Clase de apoyo que carga todos los elementos web necesarios por las librerias
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ResourcesLoader {
    private static final Logger DEBUGGER = Logger.getLogger(ResourcesLoader.class);
    
    /**
     * Clave con la que se localiza la ruta de la hoja de estilos base
     */
    public static String PATH_CSS="/neoAtlantis/resources/css/NA_styles.css";
    /**
     * Clave con la que se localiza la ruta de la imagen de error
     */
    public static String PATH_ERROR_IMAGE="/neoAtlantis/resources/images/error.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de ok
     */
    public static String PATH_OK_IMAGE="/neoAtlantis/resources/images/ok.png";
    /**
     * Clave con la que se localiza la ruta del la imagen de llaves
     */
    public static String PATH_KEYS_IMAGE="/neoAtlantis/resources/images/keys.png";
    /**
     * Clave con la que se localiza la ruta del archivo de scripts base
     */
    public static String PATH_UTILS_JS="/neoAtlantis/resources/scripts/NA_utils.js";
    /**
     * Clave con la que se localiza la ruta de la imagen de primero
     */
    public static String PATH_FIRST_IMAGE="/neoAtlantis/resources/images/firstIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de anterior
     */
    public static String PATH_PREVIOUS_IMAGE="/neoAtlantis/resources/images/previousIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de siguiente
     */
    public static String PATH_NEXT_IMAGE="/neoAtlantis/resources/images/nextIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de ultima
     */
    public static String PATH_LAST_IMAGE="/neoAtlantis/resources/images/lastIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de filtro
     */
    public static String PATH_FILTER_IMAGE="/neoAtlantis/resources/images/filterIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de ordenamiento ascendente
     */
    public static String PATH_ASCENDING_IMAGE="/neoAtlantis/resources/images/ascendingIcon.png";
    /**
     * Clave con la que se localiza la ruta de la imagen de ordenamiento descendente
     */
    public static String PATH_DESCENDING_IMAGE="/neoAtlantis/resources/images/descendingIcon.png";
    /**
     * Clave con la que se localiza la ruta del login para el acceso
     */
    public static String PATH_CAPTCHA="/neoAtlantis/resources/images/catpcha.jpg";


    
    
    // Metodos publicos estaticos-----------------------------------------------

    public static void loadResorces(ServletContext context){        
        //publica la hoja de estilos
        ElementPublisherServlet servlet=new ElementPublisherServlet(ResourcesLoader.class.getResourceAsStream("default.css"), "NA_styles.css", "text/stylesheet");
        Dynamic dim=context.addServlet("HojaEstilosNA", servlet);
        dim.addMapping(PATH_CSS);
        dim.setAsyncSupported(true);
        DEBUGGER.debug("Publico la hoja de estilos en: "+PATH_CSS);
        
        //publica el script de utilerias
        servlet=new ElementPublisherServlet(ResourcesLoader.class.getResourceAsStream("utils.js"), "NA_utils.js", "text/javascript");
        dim=context.addServlet("ScriptUtils", servlet);
        dim.addMapping(PATH_UTILS_JS);
        dim.setAsyncSupported(true);
        DEBUGGER.debug("Publico la el script de utils en: "+PATH_UTILS_JS);

        //publica el servlet para el captcha
        dim=context.addServlet("CaptchaNA", new CaptchaImageServlet());
        dim.addMapping(PATH_CAPTCHA);
        dim.setAsyncSupported(true);
        DEBUGGER.debug("Publica el Captcha en: "+PATH_CAPTCHA);

        //publica la imagen de error
        publishImagePng(context, "error.png", "ImageError", PATH_ERROR_IMAGE);
        //publica la imagen de ok
        publishImagePng(context, "ok.png", "ImageOK", PATH_OK_IMAGE);
        //publica la imagen de llaves
        publishImagePng(context, "keys.png", "ImageKeys", PATH_KEYS_IMAGE);
        //publica la imagen de primero
        publishImagePng(context, "first.png", "ImageFirst", PATH_FIRST_IMAGE);
        //publica la imagen de anterior
        publishImagePng(context, "next.png", "ImageNext", PATH_NEXT_IMAGE);
        //publica la imagen de siguiente
        publishImagePng(context, "previous.png", "ImagePrevious", PATH_PREVIOUS_IMAGE);
        //publica la imagen de ultimo
        publishImagePng(context, "last.png", "ImageLast", PATH_LAST_IMAGE);
        //publica la imagen de ultimo
        publishImagePng(context, "filter.png", "ImageFilter", PATH_FILTER_IMAGE);
        //publica la imagen de ultimo
        publishImagePng(context, "ascending.png", "ImageAscending", PATH_ASCENDING_IMAGE);
        //publica la imagen de ultimo
        publishImagePng(context, "descending.png", "ImageDescending", PATH_DESCENDING_IMAGE);
    }
    
    public static void publishImagePng(ServletContext context, String image, String servletName, String path){
        ElementPublisherServlet servlet;
        Dynamic dim;

        servlet=new ElementPublisherServlet(ResourcesLoader.class.getResourceAsStream(image), image, "image/png");
        dim=context.addServlet(servletName, servlet);
        dim.addMapping(path);
        dim.setAsyncSupported(true);
        DEBUGGER.debug("Publico la imagen de "+image+" en: "+path);
    }
}
