package neoatlantis.applications.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import neoatlantis.applications.exceptions.ApplicationException;
import neoatlantis.applications.parameters.interfaces.MemoryParametersLoader;
import neoatlantis.applications.parameters.objects.MemoryParameters;
import neoatlantis.applications.parameters.objects.ParameterType;
import neoatlantis.applications.printer.SimpleHtmlParameterAdministratorPrinter;
import neoatlantis.applications.printer.interfaces.LogViewerPrinter;
import neoatlantis.applications.printer.interfaces.NavigationPrinter;
import neoatlantis.applications.printer.interfaces.ParameterAdministratorPrinter;
import neoatlantis.applications.utils.DebugerRequestHtml;
import neoatlantis.applications.utils.UtilsApplication;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.listeners.PageListener;
import neoatlantis.applications.web.objects.NavigationalState;
import neoatlantis.applications.web.utils.ResourcesLoader;
import neoatlantis.utils.configurations.ConfigurationUtils;
import neoatlantis.utils.data.DataUtils;
import neoatlantis.utils.data.interfaces.ConfirmationCode;
import org.apache.log4j.Logger;

/**
 * Clase de apoyo que permite el acceso a los objetos del contexto de NA para aplicaciones
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class UtilsApplicationBean {
    private static final Logger DEBUGER = Logger.getLogger(UtilsApplicationBean.class);    
//    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
    
    public static final String OPERATION_PARAM="NA_Operation";
    public static final String CAPTCHA_IMAGE_ID="NA:Captcha_Image";
    public static MemoryParameters params;

    
    
    
    //moviles-------------------------------------------------------------------
    
    public static boolean isMobile(HttpServletRequest request){
        return UtilsApplication.isMovil(PageListener.getOperatingSystem(request));
    }
    
    
    
    
    //debug---------------------------------------------------------------------
    
    public static String getDebug(HttpServletRequest request) {
        return DebugerRequestHtml.getDebug(request, true);
    }

    
    
    
    
    //pagina---------------------------------------------------------------------

    public static String getPageGenerationTime(HttpServletRequest request){
        //recupero la fecha de inicio de la generacion de la pagina
        try{
            Long ini=(Long)request.getAttribute(PageListener.LOAD_TIME_KEY);
            //calculo el tiempo en segundos
            double t=((double)((new Date()).getTime()-ini)/1000);

            //regreso una cadena de comentario con el dato
            return  "<!--  La pagina actual se tardo "+t+" seg. en generarse -->" ;
        }
        catch(Exception ex){
            return "<!--  El tiempo de generación de la página no se logró definir -->";
        }
    }
    
    
    
    
    //navegacion----------------------------------------------------------------

    public static String getPagination(HttpServletRequest request, String formName){
        NavigationPrinter printer=(NavigationPrinter)request.getServletContext().getAttribute(ApplicationListener.NAVIGATION_PRINTER_KEY);      
        
        //valida que exista el printer
        if( request==null ){
            return "<div class=\"NA_General_textoError\">No se proporciono el request Web</div>\n";
        }
        else if( formName==null || formName.isEmpty() ){
            return "<div class=\"NA_General_textoError\">No se proporciono el nombre de la forma</div>\n";
        }
        else if( printer!=null ){
            HashMap<String,Object> params=new HashMap<String,Object>();
            params.put(SimpleHtmlParameterAdministratorPrinter.REQUEST_KEY, request);
            params.put(PageListener.FORM_NAME_KEY, formName);
            
            DEBUGER.debug("Pinto la paginacion.");
            return (String)printer.printPagination(params);
        }
        else{
            DEBUGER.error("No existe Printer para desplegar la Paginación");
            
            return "<div class=\"NA_General_textoError\">No existe un Printer para la Paginación en el contexto</div>\n";
        }
    }
    
    public static String getOrderOption(HttpServletRequest request, String formName, String label, String column){
        NavigationPrinter printer=(NavigationPrinter)request.getServletContext().getAttribute(ApplicationListener.NAVIGATION_PRINTER_KEY);      

        //valida que exista el printer
        if( request==null ){
            return "<div class=\"NA_General_textoError\">No se proporciono el request Web</div>\n";
        }
        else if( formName==null || formName.isEmpty() ){
            return "<div class=\"NA_General_textoError\">No se proporciono el nombre de la forma</div>\n";
        }
        else if( label==null || label.isEmpty() ){
            return "<div class=\"NA_General_textoError\">No se proporciono la etiqueta para la opción</div>\n";
        }
        else if( column==null || column.isEmpty() ){
            return "<div class=\"NA_General_textoError\">No se proporciono la columna de la ordenación</div>\n";
        }
        else if( printer!=null ){
            HashMap<String,Object> params=new HashMap<String,Object>();
            params.put(SimpleHtmlParameterAdministratorPrinter.REQUEST_KEY, request);
            params.put(PageListener.FORM_NAME_KEY, formName);
            params.put(PageListener.COLUMN_KEY, column);
            params.put(PageListener.TEXT_KEY, label);
            
            DEBUGER.debug("Pinto la opción de ordenacion: "+column);
            return (String)printer.printOrderOption(params);
        }
        else{
            DEBUGER.error("No existe Printer para desplegar la Opción de Ordenamiento");
            
            return "<div class=\"NA_General_textoError\">No existe un Printer para la Opción de Ordenamiento  en el contexto</div>\n";
        }
    }
    
    public static void setFilterValue(HttpServletRequest request, String filter, String value){
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null){
            ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().put(filter, value);
        }
    }
    
    public static boolean isFilterAplicated(HttpServletRequest request){
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null &&
               !((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().isEmpty() ){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static String getStateFilterValue(HttpServletRequest request, String filtro){
        DEBUGER.debug("Recupera valor del filtro '"+filtro);
        
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null &&
               ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().containsKey(filtro)){
            return  ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().get(filtro);
        }
        else {
            DEBUGER.debug("No se logro recuperar el estado");
            return "";
        }
    }

    public static String getStateFilterValue(HttpServletRequest request, String filtro, String defaultValue){
        DEBUGER.debug("Recupera valor del filtro con opcion de default '"+filtro);
        
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null &&
               ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().containsKey(filtro)){
            DEBUGER.debug("Valor actual del filtro '"+filtro+"': "+((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().get(filtro));
            
            if(  ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().get(filtro)==null ||
                     ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().get(filtro).isEmpty()){
                return defaultValue;
            }
            else{
                return  ((NavigationalState)request.getSession().getAttribute(PageListener.STATE_KEY)).getFilters().get(filtro);
            }
        }
        else {
            DEBUGER.debug("No se logro recuperar el estado");
            return defaultValue;
        }
    }
    
    public static String getOptionForCombo(HttpServletRequest request, String filtro, String id, String text){
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null ){
            return "<option value=\""+id+"\""+(getStateFilterValue(request, filtro).equals(id)? "selected": "")+">"+text+"</option>";
        }
        else return "";        
    }
    
    public static String getDefaultRadioButton(HttpServletRequest request, String filtro,String text){
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null ){
            return "<input type=\"radio\" id=\"NA_FilterValue\" name=\"NA_FilterValue\" value=\" \" "+(getStateFilterValue(request, filtro).trim().isEmpty()? "checked": "")+" />"+text;
        }
        else return "";        
    }
    
    public static String getRadioButton(HttpServletRequest request, String filtro, String value, String text){
        if( request!=null && request.getSession().getAttribute(PageListener.STATE_KEY)!=null ){
            return "<input type=\"radio\" id=\"NA_FilterValue\" name=\"NA_FilterValue\" value=\""+value+"\" "+(getStateFilterValue(request, filtro).equals(value)? "checked": "")+" />"+text;
        }
        else return "";        
    }
        
    
    
    //parametros----------------------------------------------------------------
    
    public static String getParameter(HttpServletRequest request, String parametro) throws ApplicationException {
        try{
            MemoryParameters mp=(MemoryParameters) request.getSession().getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);

            return mp.getParameterValue(parametro);
        }
        catch(Exception ex){
            throw new ApplicationException(ApplicationException.APP_CONFIGURATION, "No se logro consultar el parametro: "+parametro, ex);
        }
    }

    public int getNumberParameter(HttpServletRequest request, String parametro) throws ApplicationException {
        try{
            MemoryParameters mp = (MemoryParameters) request.getSession().getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);

            if (mp.getParameter(parametro).getType()== ParameterType.NUMERIC) {
                try{
                    return Integer.parseInt(mp.getParameterValue(parametro));
                }catch(Exception ex){
                    return 0;
                }
            }

            return -1;
        }
        catch(Exception ex){
            throw new ApplicationException(ApplicationException.APP_CONFIGURATION, "No se logro consultar parametro: "+parametro, ex);
        }
    }

    public static boolean getBooleanParameter(HttpServletRequest request, String parametro) throws ApplicationException {
        try{
            MemoryParameters mp = (MemoryParameters) request.getSession().getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);

            if (mp.getParameter(parametro).getType() == ParameterType.BOOLEAN) {
                return DataUtils.validateTrueBoolean(mp.getParameterValue(parametro));
            }

            return false;
        }
        catch(Exception ex){
            throw new ApplicationException(ApplicationException.APP_CONFIGURATION, "No se logro consultar el parametro: "+parametro, ex);
        }
    }

    public static String getParameterFromLoader(Class appClass, String parametro){
        try{
            //recupero los parametros
            Properties p=getParametersFromLoader(appClass);
            
            return p.getProperty(parametro);
        }
        catch(Exception ex){
            DEBUGER.error("No se logro leer el parametro '"+parametro+"'.", ex);
            return null;
        }
    }
    
    public static Properties getParametersFromLoader(Class appClass){
        try{
            Properties p=new Properties();
            HashMap<String,Object> entorno=new HashMap<String,Object>();
            
            //valido si existen en memoria
            if( params!=null ){
                DEBUGER.debug("Trabajo con los de memoria");
            }
            else{
                //obtengo la ruta de la aplicación
                String appPath=UtilsApplication.getAppPath(appClass);
                DEBUGER.debug("Ruta de la aplicación: "+appPath);

                //defino los elementos por default
                entorno.put("homeWeb", appPath+"/");
                entorno.put("homeWebInf", appPath+"/WEB-INF/");
                entorno.put("homeClass", appPath+"/WEB-INF/classes/");

                //valido si se tiene un archivo propoi o es el de default
                String config=UtilsApplication.getInitParameter(appPath, "configAppNA");
                if( config!=null ){
                    config=ConfigurationUtils.parseWindcars(config, entorno);
                }
                else{
                    config=appPath+"config/NA_application.xml";
                }
                DEBUGER.debug("Archivo de configuración: "+config);

                MemoryParametersLoader pl=UtilsApplication.getParametersLoader(config, entorno);
                DEBUGER.debug("ParameterLoader cargado: "+pl);
                params=new MemoryParameters(pl);

                pl.loadParameters(params);
                DEBUGER.debug("Parametros encontrados: "+(params!=null? params.size(): -1));
            }
            
            for(int i=0; params.getParametersKeys()!=null && i<params.getParametersKeys().size(); i++){
                p.put(params.getParametersKeys().get(i), params.getParameterValue(params.getParametersKeys().get(i)));
            }
            
            return p;
        }
        catch(Exception ex){
            DEBUGER.error("No se logro leer los parametros.", ex);
            return null;
        }
    }

    
    public static String getParametersAdministration(HttpServletRequest request){
        ParameterAdministratorPrinter printer=(ParameterAdministratorPrinter)request.getServletContext().getAttribute(ApplicationListener.PARAMS_ADMIN_PRINTER_KEY);      
        MemoryParameters mp=(MemoryParameters) request.getSession().getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);
        
        //valida que exista el printer
        if( request==null ){
            return "<div class=\"NA_General_textoError\">No se proporciono el request Web</div>\n";
        }
        else if (mp == null) {
            return "<div class=\"NA_General_textoError\">No existen Parametros a administrar./div>\n";
        }
        else if( printer!=null ){
            HashMap<String,Object> params=new HashMap<String,Object>();
            params.put(SimpleHtmlParameterAdministratorPrinter.REQUEST_KEY, request);
            DEBUGER.debug("Operación solicitada: "+request.getParameter(OPERATION_PARAM));
            
            //por default muestra la lista de parametros
            //else{
                DEBUGER.debug("Pinto el listado de parametros.");
                return (String)printer.printAdministration(params);
            //}
        }
        else{
            DEBUGER.error("No existe Printer para desplegar la Administración de parametros");
            
            return "<div class=\"NA_General_textoError\">No existe un Printer para la Administración de Parametros en el contexto</div>\n";
        }
    }
    





    // log      ----------------------------------------------------------------
    
    public static String getLogViewer(HttpServletRequest request){
        LogViewerPrinter printer=(LogViewerPrinter)request.getServletContext().getAttribute(ApplicationListener.LOG_VIEWER_PRINTER_KEY);      
        
        //valida que exista el printer
        if( request==null ){
            return "<div class=\"NA_General_textoError\">No se proporciono el request Web</div>\n";
        }
        else if( printer!=null ){
            HashMap<String,Object> params=new HashMap<String,Object>();
            params.put(SimpleHtmlParameterAdministratorPrinter.REQUEST_KEY, request);
            DEBUGER.debug("Operación solicitada: "+request.getParameter(OPERATION_PARAM));
            
            //por default muestra el log
            DEBUGER.debug("Pinto el listado de eventos del log.");
            return (String)printer.printLogViewer(params);
        }
        else{
            DEBUGER.error("No existe Printer para desplegar el Visualizador de Log");
            
            return "<div class=\"NA_General_textoError\">No existe un Printer para el Visualizador de Log en el contexto</div>\n";
        }
    }
    



    
    
    
    
    public static String getScriptUtilsPath(HttpServletRequest request){
        return request.getContextPath()+ResourcesLoader.PATH_UTILS_JS;
    }


    // catpcha      ----------------------------------------------------------------
    
    public static String getCaptchaPath(HttpServletRequest request){
        return request.getContextPath()+ResourcesLoader.PATH_CAPTCHA;
    }
    
    public static  boolean validateConfirmationCode(HttpServletRequest request, String code){
        String actual=(String)request.getSession().getAttribute(ApplicationListener.CODE_CONFIRMATION_KEY);
        ConfirmationCode codigo=(ConfirmationCode)request.getServletContext().getAttribute(ApplicationListener.CODE_GENERATOR_KEY);
        
        if( codigo==null ){
            throw new RuntimeException("No se tiene deinido un ''codeGenerator.");
        }
        
        DEBUGER.debug("Codido original: "+actual);
        DEBUGER.debug("Codido a validar: "+code);
        
        if( actual!=null && actual.equalsIgnoreCase(code) ){
            return true;
        }
        else{
            request.getSession().setAttribute(ApplicationListener.CODE_CONFIRMATION_KEY, codigo.create());
            
            return false;
        }
    }
    
    public static String printHtmlCaptcha(HttpServletRequest request){
        StringBuilder sb = new StringBuilder("");

        sb.append("<div class=\"NA_Captcha\">\n");
        sb.append("<dl>\n");
        sb.append("<dt>Introduce el c&oacute;digo:</dt>\n");
        sb.append("<dd>\n");
        sb.append("<input type=\"text\" id=\"").append(PageListener.CAPTCHA_PARAM).append("\" name=\"").append(PageListener.CAPTCHA_PARAM).append("\" placeholder=\"Ingresa el c&oacute;digo\" />\n");
        sb.append("</dd>\n");
        sb.append("<dt>C&oacute;digo:</dt>\n");
        sb.append("<dd>\n");
        sb.append("<img  id=\"").append(CAPTCHA_IMAGE_ID).append("\" src=\"").append(UtilsApplicationBean.getCaptchaPath(request)).append("\" title=\"Captcha\" alt=\"Captcha\" />\n");
        sb.append("<div class=\"NA_ReloadCaptcha\" onclick=\"NAUpdateImageCaptcha()\" title=\"Generar un nuevo c&oacute;digo\" />\n");
        sb.append("</dd>\n");
        sb.append("</dl>\n");
        sb.append("</div>\n");
        
        return sb.toString();
    }
}
