package neoatlantis.applications.web.listeners;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import neoatlantis.applications.jmx.*;
import neoatlantis.applications.parameters.interfaces.MemoryParametersLoader;
import neoatlantis.applications.parameters.objects.MemoryParameters;
import neoatlantis.applications.printer.interfaces.LogViewerPrinter;
import neoatlantis.applications.printer.interfaces.NavigationPrinter;
import neoatlantis.applications.printer.interfaces.ParameterAdministratorPrinter;
import neoatlantis.applications.web.AdminAppServlet;
import neoatlantis.applications.web.filters.EncodingFilter;
import neoatlantis.applications.web.objects.ApplicationSession;
import neoatlantis.applications.web.objects.RequestStatistics;
import neoatlantis.applications.web.utils.ResourcesLoader;
import neoatlantis.utils.captcha.interfaces.CaptchaPainter;
import neoatlantis.utils.configurations.ClassGenerator;
import neoatlantis.utils.configurations.ConfigurationUtils;
import neoatlantis.utils.data.DataUtils;
import neoatlantis.utils.data.interfaces.ConfirmationCode;
import neoatlantis.utils.web.MBeanRegister;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Objeto que carga toda la configuracion del aplicativo web
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ApplicationListener implements ServletContextListener {
    private static final Logger DEBUGGER=Logger.getLogger(ApplicationListener.class);
    private static List<String> raizLogger;
    private static List<String> omisionLogger;
    private static RequestStatistics estadisticaPagina;
    private static RequestStatistics estadisticaSesion;

    //Nombre de los elementos que se almacenan en la sesion
    public static final String DEBUG_KEY="na.util.app.debug";
    public static final String APP_NAME_KEY="na.util.app.name";
    public static final String APP_VERSION_KEY="na.util.app.version";
    public static final String SESSIONS_KEY="na.util.app.sessions";
    public static final String PARAMETERS_KEY="na.util.app.parameters";
    public static final String PAGE_STATISTICS_KEY="na.util.app.paginas.estadisticas";
    public static final String SESSION_STATISTICS_KEY="na.util.app.sesiones.estadisticas";
    public static final String LOG_PATH_KEY="na.util.app.logPath";
    public static final String PARAMS_ADMIN_PRINTER_KEY="na.util.app.printers.ParamsAdministration";
    public static final String LOG_VIEWER_PRINTER_KEY="na.util.app.printers.LogViewer";
    public static final String CAPTCHA_PAINTER_KEY="na.util.app.printers.CatcharPainter";
    public static final String NAVIGATION_PRINTER_KEY="na.util.app.printers.Navigator";

    private boolean debug=false;
    private boolean sendMail=false;
    private String rutaLog="./";
    private String ver="N/D";
    private String app="Indefinida";
    private List<ApplicationSession> sesiones;
    private boolean configLogger=false;
    private Properties configMail;
    private Element nodoAccessControler;

    static{
        //preparo la raiz del logger
        raizLogger=new ArrayList();
        raizLogger.add("neoAtlantis");
        //preparo la raiz de las omisiones
        omisionLogger=new ArrayList();
    }
        
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("INFO-NA: Inicio la carga de la configuración de la aplicación.");
        
        //recupero los datos de la aplicación
        this.loadInfoApplication(sce.getServletContext());

        //reviso si se tiene que cargar alguna configuracion
        this.loadConfig(sce.getServletContext());

        //cargo la configuración del debugger
        this.configDebugger(sce.getServletContext());
        
        //preparo el entorno
        this.initEnvironment(sce.getServletContext());
        
        //muestro la información de la carga
        this.displayInfo();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.finalizeEnvironment(sce.getServletContext());
        this.sesiones=null;
    }
    
    

    
    
    //  MS ---------------------------------------------------------------------
    
    
    /**
     * Genera la ruta al archivo de configuracion
     * @param context Contexto de la aplicacion
     * @return Cadena de la ruta al archivo de configuracion
     */
    public static String defineConfigFile(ServletContext context){
        String homeWeb = context.getRealPath("/").replace('\\', '/') + "/";
        String homeWebInf = homeWeb + "WEB-INF/";

        String configFile = homeWebInf + "config/NA_application.xml";

        //valido si existe un archivo de definicion de parametros
        if (context.getInitParameter("configAppNA") != null
                && !context.getInitParameter("configAppNA").equals("")) {
            configFile = ConfigurationUtils.parseWindcars(context.getInitParameter("configAppNA"), defineContextVars(context));
        }
        
        return configFile;
    }
    
    /**
     * Genera las variables de entorno a partir del contexto
     * @param context Contexto de la aplicacion
     * @return Arreglo con las variables
     */
    public static Map<String, Object> defineContextVars(ServletContext context){
        Map<String, Object> entorno = new HashMap<String, Object>();
        
        //configuro los home
        String homeWeb = context.getRealPath("/").replace('\\', '/') + "/";
        String homeWebInf = homeWeb + "WEB-INF/";

        entorno.put("appContext", context);
        entorno.put("homeWeb", homeWeb);
        entorno.put("homeWebInf", homeWebInf);
        entorno.put("homeClass", homeWebInf + "classes/");
        
        return entorno;
    }
    
    
    
    //  MP ---------------------------------------------------------------------
    
    private void finalizeEnvironment(ServletContext context){
        MBeanServer mbs;
        
        //remueve el registro del MBean de sesiones
        MBeanRegister.unregisterMBean("neoatlantis.app."+(DataUtils.cleanSpecialCharacters(this.app))+".sessions.jmx:type=InfoStatusSessions");
        
        //remueve el registro del MBean de estadisticas de pagina
        MBeanRegister.unregisterMBean("neoatlantis.app."+(DataUtils.cleanSpecialCharacters(this.app))+".pagesReponse.jmx:type=PagesStatistics");

        //valida si se cargo el access controller
        if( this.nodoAccessControler!=null ){
            MBeanRegister.unregisterMBean("neoatlantis.app."+(DataUtils.cleanSpecialCharacters(this.app))+".users.jmx:type=InfoStatusUsers");
        }
    }
    
    /**
     * Carga y prepara el debugger
     */
    private void configDebugger(ServletContext context){
        //si ya se configuro, no hago nada
        if(this.configLogger){
            return;
        }
        
        System.out.println("INFO-NA: Carga la configuración del Debugger.");
        Properties config=new Properties();
        String[] subLoggers;
        
        //valida si esta activo el debug
        RuntimeMXBean rt=ManagementFactory.getRuntimeMXBean();
        for(String ct: rt.getInputArguments()){
            //revisa si esta activo el debug
            if( ct.equals("-Dna.debug") ){
                this.debug=true;
                context.setAttribute(DEBUG_KEY, true);
                System.out.println("INFO-NA: Localiza parametro: "+ct);
            }
            //revisa si existe una ruta para los logs
            else if( ct.indexOf("-Dna.pathLogs=")>-1 ){
                this.rutaLog=ct.substring(ct.indexOf('=')+1);
                System.out.println("INFO-NA: Localiza parametro: "+ct);
            }
            //revisa si esta activo la notificacion de errores
            else if( ct.equals("-Dna.reportErrorsByMail") ){
                this.sendMail=true;
                System.out.println("INFO-NA: Localiza parametro: "+ct);
            }
        }
        
        //revisa que la ruta termine con /
        if( this.rutaLog.isEmpty() ){
            this.rutaLog="./";
        }
        else if( !this.rutaLog.endsWith("/") ){
            this.rutaLog+="/";
        }
        this.rutaLog+=this.app.replaceAll(" ", "")+".log";
        context.setAttribute(LOG_PATH_KEY, this.rutaLog);

        //recupero la raiz del log
        if( context.getInitParameter("appClassRootLogger")!=null &&
                !context.getInitParameter("appClassRootLogger").equals("") ){
            subLoggers=context.getInitParameter("appClassRootLogger").split("\n");
            for(int i=0; subLoggers!=null&&i<subLoggers.length; i++){
                if( subLoggers[i]!=null && !subLoggers[i].trim().isEmpty() ){
                    raizLogger.add(subLoggers[i].trim());
                }
            }            
            System.out.println("INFO-NA: Carga los datos del appClassRootLogger: "+raizLogger.size());
        }

        for(String c: raizLogger){
            config.setProperty("log4j.logger."+c, (debug? "DEBUG": "ERROR")+", stdout");
        }
        for(String c: omisionLogger){
            config.setProperty("log4j.logger."+c, "OFF");
        }
        //define el debug
        config.setProperty("log4j.logger.org.apache.ibatis", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.org.apache.ibatis.common.jdbc.SimpleDataSource", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.org.apache.ibatis.common.jdbc.ScriptRunner", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.org.apache.ibatis.sqlmap.engine.impl.SqlMapClientDelegate", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.java.sql.Connection", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.java.sql.Statement", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.java.sql.PreparedStatement", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.java.sql.ResultSet", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.neoAtlantis", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        config.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        config.setProperty("log4j.appender.stdout.layout.ConversionPattern", "[%5p][%d]["+app+"][%c] %m%n");
        
        //define el log de errores
        config.setProperty("log4j.rootLogger", "ERROR, fileout");
        config.setProperty("log4j.appender.fileout", "org.apache.log4j.RollingFileAppender");
        config.setProperty("log4j.appender.fileout.File", this.rutaLog);
        config.setProperty("log4j.appender.fileout.MaxFileSize", "900000");
        config.setProperty("log4j.appender.fileout.MaxBackupIndex", "10");
        config.setProperty("log4j.appender.fileout.filter.fil1", "org.apache.log4j.varia.LevelRangeFilter");
        config.setProperty("log4j.appender.fileout.filter.fil1.LevelMin", "ERROR");
        config.setProperty("log4j.appender.fileout.filter.fil1.LevelMax", "FATAL");
        config.setProperty("log4j.appender.fileout.layout", "org.apache.log4j.PatternLayout");
        config.setProperty("log4j.appender.fileout.layout.ConversionPattern", "[%5p][%d]["+app+"][%c] %m%n");

        //define el envio de errores por mail
        if( this.sendMail && this.configMail!=null && this.configMail.getProperty("host")!=null && !this.configMail.getProperty("host").isEmpty()
                && this.configMail.getProperty("sender")!=null && !this.configMail.getProperty("sender").isEmpty()
                && this.configMail.getProperty("destination")!=null && !this.configMail.getProperty("destination").isEmpty()){
            config.setProperty("log4j.rootLogger", "ERROR, fileout, sendMail");
            config.setProperty("log4j.appender.sendMail", "org.apache.log4j.net.SMTPAppender");
            config.setProperty("log4j.appender.sendMail.SMTPHost", this.configMail.getProperty("host"));
            config.setProperty("log4j.appender.sendMail.SMTPUsername", this.configMail.getProperty("sender"));
            if( this.configMail.getProperty("password")!=null && !this.configMail.getProperty("password").isEmpty() ){
                config.setProperty("log4j.appender.sendMail.SMTPPassword", DataUtils.getCipherData(this.configMail.getProperty("password")));
            }
            config.setProperty("log4j.appender.sendMail.From", this.configMail.getProperty("sender"));
            config.setProperty("log4j.appender.sendMail.To", this.configMail.getProperty("destination"));
            config.setProperty("log4j.appender.sendMail.Subject", "Error en la aplicación '"+app+"'");
            config.setProperty("log4j.appender.sendMail.layout", "org.apache.log4j.PatternLayout");
            config.setProperty("log4j.appender.sendMail.layout.ConversionPattern", "[%d{ISO8601}]%n%n%-5p%n%n%c%n%n%m%n%n");
            System.out.println("INFO-NA: Se configura el envío de mail al detectar errores");
        }
        
        System.out.println("INFO-NA: configig log4j: "+config);
        
        PropertyConfigurator.configure(config);
        this.configLogger=true;
    }
    
    /**
     * Recupera y carga los datos de la aplicacion
     * @param context Contexto de la aplicacion
     */
    private void loadInfoApplication(ServletContext context){
        // recupero la version
        if( context.getInitParameter("appVersion")!=null &&
                !context.getInitParameter("appVersion").equals("") ){
            this.ver=context.getInitParameter("appVersion");
        }

        //recupero la aplicacion
        this.app=context.getServletContextName();

        //valido que tengan valores
        if(this.app==null) this.app="ND";
        if(this.ver==null) this.ver="0.1";
    }
    
    /**
     * Carga la configuracion definida en el archivo 
     * @param context Contexto de la aplicacion
     */
    private void loadConfig(ServletContext context){
        Map<String, Object> entorno=defineContextVars(context);
        String configFile=defineConfigFile(context);
        File configArchive;

        System.out.println("INFO-NA: Intenta acceder al archivo '" + configFile + "'.");
        
        //carga la información del tamaño de pagina
//        context.setAttribute(PAGE_SIZE_KEY, 10);

        configArchive=new File(configFile);
        if ( configArchive.canRead()) {
            System.out.println("INFO-NA: Se encontro el archivo '" + configArchive.toURI() + "', se procede a la carga configuración de la aplicación.");

            //leo el contenido del XML
            SAXBuilder sb = new SAXBuilder();
            try {
                Document doc = sb.build(configArchive.toURI().toURL());
                Element eTmp, root = doc.getRootElement();

                //carga datos generales
                if( root.getAttribute("version")!=null ){
                    this.ver=root.getAttributeValue("version");
                }
                if( root.getAttribute("name")!=null ){
                    this.app=root.getAttributeValue("name");
                }
                
                //para el logger
                this.loadLoggerConfiguration(context, root);

                //para la codificacion
                if( root.getAttribute("forceEncoding")!=null ){
                    DEBUGGER.debug("Existe forze de codificación a: "+root.getAttributeValue("forceEncoding"));
                    javax.servlet.FilterRegistration.Dynamic dim=context.addFilter("ForcingEncodingNA", new EncodingFilter(root.getAttributeValue("forceEncoding")));
                    dim.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
                }
                
                //para parametros
                eTmp = root.getChild("parameters");
                if (eTmp != null) {
                    this.loadParametersConfiguration(context, eTmp, entorno);
                }

                //para printer de  parametros
                eTmp = root.getChild("parametersAdministrationPrinter");
                if (eTmp != null) {
                    context.setAttribute(PARAMS_ADMIN_PRINTER_KEY, this.makeParameterAdministratorPrinter(eTmp, entorno));
                }

                //para printer de  log
                eTmp = root.getChild("logViewerPrinter");
                if (eTmp != null) {
                    context.setAttribute(LOG_VIEWER_PRINTER_KEY, this.makeLogViewerPrinter(eTmp, entorno));
                }

                //para printer de  navegacion
                eTmp = root.getChild("navigationPrinter");
                if (eTmp != null) {
                    context.setAttribute(NAVIGATION_PRINTER_KEY, this.makeNavigationPrinter(eTmp, entorno));
                }

                //para painter de captcha
                eTmp = root.getChild("captchaPainter");
                if (eTmp != null) {
                    context.setAttribute(CAPTCHA_PAINTER_KEY, this.makeCaptchaPainter(eTmp, entorno));
                }

                //para generador de codigos
                eTmp = root.getChild("codeGenerator");
                if (eTmp != null) {
                    context.setAttribute(CODE_GENERATOR_KEY, this.makeCodeConfirmation(eTmp, entorno));
                }
                
                //para catalogos
                /*eTmp = root.getChild("catalogs");
                if (eTmp != null) {
                    this.cargaCatalogos(context, eTmp, homeWeb, entorno);
                }
                
                //para mensajeria
                eTmp = root.getChild("messaging");
                if (eTmp != null) {
                    cargaMensajeria(context, eTmp, homeWeb, entorno);
                }*/
                
                //para autenticacion
                eTmp = root.getChild("accessController");
                if (eTmp != null) {
                    this.nodoAccessControler=eTmp;
                }                
            } catch (Exception ex) {
                System.out.println("INFO-NA: Error al cargar la configuración para la aplicación: "+ex);
                ex.printStackTrace();
            }
        } else {
            System.out.println("INFO-NA: No existe configuración para la aplicación.");
        }
    }
    
    /**
     * Prepara el entorno para su uso
     */
    private void initEnvironment(ServletContext context){
        javax.servlet.ServletRegistration.Dynamic dimRequest;
        
        //inicio la coleccion de sesiones
        this.sesiones=Collections.synchronizedList(new ArrayList<ApplicationSession>());
        estadisticaPagina=new RequestStatistics();
        estadisticaSesion=new RequestStatistics();
        
        //cargo las variables en el contexto
        context.setAttribute(SESSIONS_KEY, this.sesiones);
        context.setAttribute(PAGE_STATISTICS_KEY, estadisticaPagina);
        context.setAttribute(SESSION_STATISTICS_KEY, estadisticaSesion);
        context.setAttribute(APP_NAME_KEY, this.app);
        context.setAttribute(APP_VERSION_KEY, this.ver);

        //genero los listener para sesion y pagina
        DEBUGGER.debug("Carga el escuchador para sesiones.");
        context.addListener(SessionListener.class);
        DEBUGGER.debug("Carga el escuchador para paginas.");
        context.addListener(PageListener.class);

        //cargo los recursos web
        ResourcesLoader.loadResorces(context);

        //publico los servicios de administracion de aplicacion
        dimRequest=context.addServlet("AppAdministrationNA", new AdminAppServlet());
        dimRequest.addMapping(AdminAppServlet.PATH_SERVICE);
        dimRequest.setAsyncSupported(true);
        DEBUGGER.debug("Publica el Servicio de Administración de aplicación en: "+AdminAppServlet.PATH_SERVICE);
        
        //valido si se debe de cargar el acces controller
        if(this.nodoAccessControler!=null){
                    DEBUGGER.debug("Configura el Access Controller.");
                    new AccessControllerPublisher(this.nodoAccessControler, context);
        }
        
        //carga el MBean de la sesion
        MBeanRegister.registerMBean(new InfoStatusSessionsMBean(context), InfoStatusSessions.class, "neoatlantis.app."+(DataUtils.cleanSpecialCharacters(this.app))+".sessions.jmx:type=InfoStatusSessions");
        //carga el MBean de las estadisticas de pagina
        MBeanRegister.registerMBean(new PagesStatisticsMBean(context), PagesStatistics.class, "neoatlantis.app."+(DataUtils.cleanSpecialCharacters(this.app))+".pagesReponse.jmx:type=PagesStatistics");
    }

    /**
     * Despliega el detalle de la aplicación y configuración cargada
     */
    private void displayInfo(){
        System.out.println("INFO: Inicia Aplicación '"+this.app+"' V. "+this.ver);
        System.out.println("INFO: Estado del debug = "+this.debug);
        System.out.println("INFO: Ruta del Log = "+this.rutaLog);
        System.out.print("INFO: Raiz de las clases = ");
        for(int i=0; i<this.raizLogger.size(); i++){
            System.out.print(this.raizLogger.get(i)+" ");
        }
        System.out.print("INFO: Omisiones = ");
        for(int i=0; i<this.omisionLogger.size(); i++){
            System.out.print(this.omisionLogger.get(i)+" ");
        }
        System.out.println();
    }
    
    /**
     * Carga la configuración de parametros en memoria
     * @param context Contexto Web de la aplicación
     * @param nodo Elemento XML de donde lee la configuración
     * @param entorno Valores del entrono de la aplicación
     */
    private void loadParametersConfiguration(ServletContext context, final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga de parametros en cache.");
        
        Object obj=ClassGenerator.createInstance(nodo, entorno);

        if(obj!=null){            
            //cargo datos
            try{
                MemoryParameters mp=new MemoryParameters((MemoryParametersLoader)obj);
                ((MemoryParametersLoader)obj).loadParameters(mp);
                context.setAttribute(PARAMETERS_KEY, mp);
                DEBUGGER.info(mp.size()+" Parametros cargados en memoria.");
                DEBUGGER.debug(mp);
            }
            catch(Exception ex){
                DEBUGGER.error("No se lograron cargar los parametros en cache.", ex);
            }
        }
        else{
            throw new RuntimeException("No se logro definir la clase de parametersLoader.");
        }
    }
    
    private ParameterAdministratorPrinter makeParameterAdministratorPrinter(final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga printer de parametros.");

        Object obj;
        obj=ClassGenerator.createInstance(nodo, entorno);

        DEBUGGER.debug("Impresor de administracion de parametros generado: "+(obj==null? "": obj.getClass()));
        return (ParameterAdministratorPrinter)obj;
    }

    private LogViewerPrinter makeLogViewerPrinter(final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga printer de log.");

        Object obj;
        obj=ClassGenerator.createInstance(nodo, entorno);

        DEBUGGER.debug("Impresor de visualizador de log generado: "+(obj==null? "": obj.getClass()));
        return (LogViewerPrinter)obj;
    }

    private NavigationPrinter makeNavigationPrinter(final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga printer de navegacion.");

        Object obj;
        obj=ClassGenerator.createInstance(nodo, entorno);

        DEBUGGER.debug("Impresor de visualizador de navegacion  generado: "+(obj==null? "": obj.getClass()));
        return (NavigationPrinter)obj;
    }

    private CaptchaPainter makeCaptchaPainter(final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga painter de captchas.");

        Object obj;
        obj=ClassGenerator.createInstance(nodo, entorno);

        DEBUGGER.debug("Dibujador de captchas generado: "+(obj==null? "": obj.getClass()));
        return (CaptchaPainter)obj;
    }

    private ConfirmationCode makeCodeConfirmation(final Element nodo, final Map<String, Object> entorno){
        DEBUGGER.debug("Procede a la carga generador de codigos.");

        Object obj;
        obj=ClassGenerator.createInstance(nodo, entorno);

        DEBUGGER.debug("Generador de codigos generado: "+(obj==null? "": obj.getClass()));
        return (ConfirmationCode)obj;
    }

    /**
     * Configura y prepara al logger de errores y debug
     * @param context Contexto de la aplicación web
     * @param root Nodo con la configuración
     */
    private void loadLoggerConfiguration(ServletContext context, final Element root){
        Element eTmp;
        List<Element> lTmp;

        //cargo los paquetes necesarios para debug
        eTmp = root.getChild("logerClass");
        if (eTmp != null) {
            // limpio lo existente
            for (int i = 1; i < raizLogger.size(); i++) {
                raizLogger.remove(i);
            }

            //agrego los nuevos
            lTmp = eTmp.getChildren("package");
            for (int i = 0; lTmp != null && i < lTmp.size(); i++) {
                if (lTmp != null && lTmp.get(i).getText() != null && !lTmp.get(i).getText().trim().isEmpty()) {
                    raizLogger.add(lTmp.get(i).getText());
                }
            }

            //cargo las omisiones
            lTmp = eTmp.getChildren("omission");
            for (int i = 0; lTmp != null && i < lTmp.size(); i++) {
                if (lTmp != null && lTmp.get(i).getText() != null && !lTmp.get(i).getText().trim().isEmpty()) {
                    omisionLogger.add(lTmp.get(i).getText());
                }
            }
        }
        
        //verifico si existe envio de correos
        System.out.println("INFO-NA: Verifica si se tiene que notificar por correo. sendMail="+this.sendMail+", reportErrors="+(eTmp!=null? eTmp.getAttribute("active"): "null"));
        eTmp = root.getChild("reportErrors");
        if( eTmp!=null ){
            System.out.println("INFO-NA: Intenta carga la configuración del las notificaciones de errores por mail.");
            
            this.configMail=new Properties();
            if(eTmp.getChild("mailHost")!=null){
                this.configMail.setProperty("host", eTmp.getChild("mailHost").getText());
            }
            if(eTmp.getChild("mailSender")!=null){
                this.configMail.setProperty("sender", eTmp.getChild("mailSender").getText());
            }
            if(eTmp.getChild("mailPassword")!=null){
                this.configMail.setProperty("password", eTmp.getChild("mailPassword").getText());
            }
            if(eTmp.getChild("mailDestination")!=null){
                this.configMail.setProperty("destination", eTmp.getChild("mailDestination").getText());
            }
            if(eTmp.getAttribute("active")!=null && DataUtils.validateTrueBoolean(eTmp.getAttributeValue("active"))){
                this.sendMail=true;
            }
        }

        //configuro el logger
        this.configDebugger(context);
    }
        
}
