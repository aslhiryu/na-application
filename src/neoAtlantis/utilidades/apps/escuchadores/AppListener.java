package neoAtlantis.utilidades.apps.escuchadores;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import neoAtlantis.utilidades.apps.catalogs.interfaces.CatalogsLoader;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryColumn;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryTable;
import neoAtlantis.utilidades.apps.catalogs.objects.TypeOrder;
import neoAtlantis.utilidades.apps.messages.MessagingControl;
import neoAtlantis.utilidades.apps.messages.postOffice.MemoryPostOffice;
import neoAtlantis.utilidades.apps.messages.postOffice.interfaces.PostOffice;
import neoAtlantis.utilidades.apps.objects.Browser;
import neoAtlantis.utilidades.apps.objects.OperatingSystem;
import neoAtlantis.utilidades.apps.objects.SesionApp;
import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameters;
import neoAtlantis.utilidades.apps.parameters.interfaces.ParametersLoader;
import neoAtlantis.utilidades.configFiles.ClassGenerator;
import neoAtlantis.utilidades.statistics.StatictisMotor;
import neoAtlantis.utilidades.statistics.interfaces.Statistical;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

/**
 * Clase Que carga toda la configuracion del aplicativo
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class AppListener implements ServletContextListener {
    private static final Logger debugger=Logger.getLogger(AppListener.class);

    public static final String APP_NOMBRE="neotlantis.app.nombre";
    public static final String APP_VERSION="neotlantis.app.version";
    public static final String APP_ID="neotlantis.app.id";
    public static final String APP_SESIONES="neotlantis.app.sesiones";
    public static final String APP_DEBUG="neotlantis.app.debug";
    public static final String APP_CATALOGOS="neotlantis.app.catalogos";
    public static final String APP_PARAMETROS="neotlantis.app.parametros";
    public static final String APP_MENSAJERIA="neotlantis.app.mensajeria";
    public static final String APP_POST_OFFICE="neotlantis.app.postOfficeMemory";
    public static final String APP_STATISTICS_MOTOR="na.app.statistics.Motor";

    private boolean debug=false;
    private String app;
    private String ver="0.1";
    private String raiz="neoAtlantis";
    private ArrayList<SesionApp> sesiones;
    private String rutaLog="./";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //valida si esta activo el debug
        RuntimeMXBean rt=ManagementFactory.getRuntimeMXBean();
        for(String ct: rt.getInputArguments()){
            //revisa si esta activo el debug
            if( ct.equals("-Dna.debug") ){
                this.debug=true;
                sce.getServletContext().setAttribute(APP_DEBUG, true);
            }
            //revisa si existe nua ruta para los logs
            else if( ct.indexOf("-Dna.pathLogs=")>-1 ){
                this.rutaLog=ct.substring(ct.indexOf('=')+1);
            }
        }
        
        //recupero la version
        if( sce.getServletContext().getInitParameter("appVersion")!=null &&
                !sce.getServletContext().getInitParameter("appVersion").equals("") ){
            this.ver=sce.getServletContext().getInitParameter("appVersion");
        }
        //recupero la raiz del log
        if( sce.getServletContext().getInitParameter("appClassRoot")!=null &&
                !sce.getServletContext().getInitParameter("appClassRoot").equals("") ){
            this.raiz=sce.getServletContext().getInitParameter("appClassRoot");
        }
        this.app=sce.getServletContext().getServletContextName();
        
        //valido que tengan valores
        if(this.app==null) this.app="ND";
        if(this.ver==null) this.ver="0.1";
        
        //inicio la coleccion de sesiones
        this.sesiones=new ArrayList<SesionApp>();
        
        //preparo el log para la aplicacion
        this.preparaLogger(this.raiz);        
        
        //prepara la configuracion de la aplicacion
        this.cargaConfiguracion(sce.getServletContext());
        
        //muestro la información de la carga
        this.despliegaInfo();
        
        //cargo las variables en el contexto
        sce.getServletContext().setAttribute(APP_SESIONES, this.sesiones);
        sce.getServletContext().setAttribute(APP_NOMBRE, this.app);
        sce.getServletContext().setAttribute(APP_VERSION, this.ver);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.sesiones=null;
    }

    //--------------------------------------------------------------------------
    
    public static void asignaIpSesion(HttpSession ses, String ip){
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)ses.getServletContext().getAttribute(APP_SESIONES);
        debugger.debug("Intento asignar IP.");
        debugger.debug("Tiempo de vida de la sessión: "+ses.getMaxInactiveInterval());
        
        
        for(SesionApp s: sesTmp){
            if( s.getId().equals(ses.getId()) && s.getIp().equals("0.0.0.0") ){
                s.setIp(ip);
                debugger.debug("Se asigna la IP "+s.getIp()+" a sesión "+s.getId()+".");
                break;
            }
        }
    }

    public static void asignaSoSesion(HttpServletRequest req){
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)req.getSession().getServletContext().getAttribute(APP_SESIONES);
        
        for(SesionApp s: sesTmp){
            if( s.getId().equals(req.getSession().getId()) && s.getBrowser()==null ){
                debugger.debug("User-Agent: "+req.getHeader("user-agent")+".");
                
                s.setOs(getSistema(req.getHeader("user-agent")));
                s.setBrowser(getNavegador(req.getHeader("user-agent")));
                debugger.debug("El sistema operativo es  "+s.getOs()+" en la sesión "+s.getId()+".");
                debugger.debug("El navegador es  "+s.getBrowser()+" en la sesión "+s.getId()+".");
                break;
            }
        }
    }
    
    public static OperatingSystem getSistemaActual(HttpSession ses){
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)ses.getServletContext().getAttribute(APP_SESIONES);
        
        for(SesionApp s: sesTmp){
            if( s.getId().equals(ses.getId()) ){
                return (s.getOs()==null? OperatingSystem.OTRO: s.getOs());
            }
        }
        
        throw new RuntimeException("No se encontro el SO de la sesion: "+ses.getId());
    }
    
    public static Browser getNavegadorActual(HttpSession ses){
        ArrayList<SesionApp> sesTmp=(ArrayList<SesionApp>)ses.getServletContext().getAttribute(APP_SESIONES);
        
        for(SesionApp s: sesTmp){
            if( s.getId().equals(ses.getId()) ){
                return s.getBrowser();
            }
        }
        
        throw new RuntimeException("No se encontro el Navegador de la sesion: "+ses.getId());
    }
    
    public static OperatingSystem getSistemaActual(HttpServletRequest req){
        asignaSoSesion(req);
        
        return getSistemaActual(req.getSession());
    }
    
    public static Browser getNavegadorActual(HttpServletRequest req){
        asignaSoSesion(req);
        
        return getNavegadorActual(req.getSession());
    }
    
    //------------------------------------ metodos privados --------------------------
    
    private void despliegaInfo(){
        System.out.println("INFO: Inicia Aplicación '"+this.app+"' V. "+this.ver);
        System.out.println("INFO: Estado del debug = "+this.debug);
        System.out.println("INFO: Ruta del Log = "+this.rutaLog);
        System.out.println("INFO: Raiz de las clases = "+this.raiz);
    }
    
    private void cargaConfiguracion(ServletContext context) {
        //configuro los home
        String homeWeb = context.getRealPath("/").replace('\\', '/') + "/";
        String homeWebInf = homeWeb + "WEB-INF/";

        Properties com = new Properties();
        com.setProperty("homeWeb", homeWeb);
        com.setProperty("homeWebInf", homeWebInf);
        com.setProperty("homeClass", homeWebInf + "classes/");

        Map<String, Object> entorno = new HashMap<String, Object>();
        entorno.put("appContext", context);
        entorno.put("homeWeb", homeWeb);

        String configFile = homeWebInf + "NA_app.xml";

        //valido si existe un archivo de definicion de parametros
        if (context.getInitParameter("configAppNA") != null
                && !context.getInitParameter("configAppNA").equals("")) {
            configFile = ClassGenerator.parseaComodinesConfig(context.getInitParameter("configAppNA"), com);
        }
        debugger.debug("Intenta acceder al archivo '" + configFile + "'.");

        if ((new File(configFile)).canRead()) {
            debugger.debug("Se encontro el archivo '" + configFile + "', se procede a la carga configuración de la aplicación.");

            //leo el contenido del XML
            SAXBuilder sb = new SAXBuilder();
            try {
                Document doc = sb.build(configFile);
                Element eTmp, root = doc.getRootElement();

                //para parametros
                eTmp = root.getChild("parameters");
                if (eTmp != null) {
                    this.cargaParametros(context, eTmp, homeWeb, entorno);
                }

                //para catalogos
                eTmp = root.getChild("catalogs");
                if (eTmp != null) {
                    this.cargaCatalogos(context, eTmp, homeWeb, entorno);
                }
                
                //para mensajeria
                eTmp = root.getChild("messaging");
                if (eTmp != null) {
                    cargaMensajeria(context, eTmp, homeWeb, entorno);

                }
                
                //para estadisticas
                eTmp = root.getChild("statistics");
                if (eTmp != null) {
                    this.iniciaEstaditicas(context, eTmp, homeWeb, entorno);

                }
            } catch (Exception ex) {
                debugger.error("Error al cargar la configuración de catalogos.", ex);
            }
        } else {
            debugger.debug("No existe configuración para la aplicación.");
        }
    }

    private void iniciaEstaditicas(ServletContext context, Element e, String homeWeb, Map entorno) {
        Object obj;
        StatictisMotor sm=StatictisMotor.getInstance();
        Statistical s;
        int t=15;
        
        debugger.debug("Si existen estadisticas, las inicia.");
        if (context.getAttribute(APP_STATISTICS_MOTOR) == null) {
            context.setAttribute(APP_STATISTICS_MOTOR, sm);
        }
        
        try {
            t=Integer.parseInt(e.getAttributeValue("interval"));

        } catch (Exception ex) {}
                
        obj = ClassGenerator.generaInstancia(e, new ArrayList(), ClassGenerator.generaComodinesHomeWeb(homeWeb), entorno);
        if (obj != null) {
            s=(Statistical)obj;
            s.setTime(t);
            sm.addStatistical(s);
            debugger.debug("Genera Estadisticas tipo '"+s.getClass().getName()+"' con intervalo de "+s.getTime()+".");
        }

    }
    
    private void cargaMensajeria(ServletContext context, Element nodo, String homeWeb, Map<java.lang.String, java.lang.Object> entorno) {
        List<Element> lTmp;
        MessagingControl ctrl=new MessagingControl();
        Object obj;
        PostOffice po;
        
        debugger.debug("Procede a la generación de las oficinas postales.");
        context.setAttribute(APP_MENSAJERIA, ctrl);
        lTmp = nodo.getChildren("postOffice");
        for (int i = 0; lTmp != null && i < lTmp.size(); i++) {
            if( lTmp.get(i).getAttribute("id")==null || lTmp.get(i).getAttributeValue("id").isEmpty()!=false ) {
                throw new RuntimeException("No se definió un ID para la oficina.");
            }
            else if( lTmp.get(i).getAttributeValue("class").equals("neoAtlantis.utilidades.apps.messages.postOffice.MemoryPostOffice")!= false ){
                context.setAttribute(APP_POST_OFFICE, new ArrayList());
                po = new MemoryPostOffice((List)context.getAttribute(APP_POST_OFFICE));
            } 
            else {
                obj = ClassGenerator.generaInstancia((Element) lTmp.get(i), new ArrayList(), ClassGenerator.generaComodinesHomeWeb(homeWeb), entorno);
                if (obj == null) {
                    throw new RuntimeException("No se logro definir la clase de postOffice.");
                }
                po = (PostOffice) obj;
            }
            ctrl.addPostOffice(lTmp.get(i).getAttributeValue("id"), po);
        }

    }
    
    private void cargaParametros(ServletContext context, Element nodo, String homeWeb,Map<String, Object> entorno){
        debugger.debug("Procede a la carga de parametros en cache.");
        
        Object obj=ClassGenerator.generaInstancia(nodo, new ArrayList(), ClassGenerator.generaComodinesHomeWeb(homeWeb), entorno);

        if(obj!=null){            
            //cargo datos
            try{
                MemoryParameters mp=new MemoryParameters((ParametersLoader)obj);
                ((ParametersLoader)obj).loadParameters(mp);
                context.setAttribute(APP_PARAMETROS, mp);
                debugger.info(mp.size()+" Parametros cargados en memoria.");
                debugger.debug(mp);
                mp=null;
            }
            catch(Exception ex){
                debugger.error("No se lograron cargar los parametros en cache.", ex);
            }
        }
        else{
            throw new RuntimeException("No se logro definir la clase de parametersLoader.");
        }
    }
    
    private void cargaCatalogos(ServletContext context, Element nodo, String homeWeb,Map<String, Object> entorno){
        debugger.debug("Procede a la carga de catalogos en cache.");

        Object obj=ClassGenerator.generaInstancia(nodo.getChild("loader"), new ArrayList(), ClassGenerator.generaComodinesHomeWeb(homeWeb), entorno);
        
        if(obj!=null){
            //cargo datos
            try{
                MemoryCatalogs mc=parseaConfiguracionCatalogos(nodo, (CatalogsLoader)obj);
                mc=((CatalogsLoader)obj).loadCatalogs(mc);
                context.setAttribute(APP_CATALOGOS, mc);
                debugger.info(mc.size()+" Catalogos cargados en memoria.");
                debugger.debug(mc);
                mc=null;
            }
            catch(Exception ex){
                debugger.error("No se lograron cargar los catalogos en cache.", ex);
            }
        }
        else{
            throw new RuntimeException("No se logro definir la clase de catalogsLoader.");
        }
    }

    private MemoryCatalogs parseaConfiguracionCatalogos(Element nodo, CatalogsLoader loader){
        MemoryCatalogs mc=new MemoryCatalogs(loader);
        List<Element> lTmp, lTmp2;
        MemoryTable mTmp;
        MemoryColumn cTmp;

        //recupera los catalogos a trabajar
        lTmp=nodo.getChildren("catalog");
        for(Element eTmp: lTmp){
            if( eTmp.getAttribute("object")==null || eTmp.getAttributeValue("object").isEmpty() ){
                throw new RuntimeException("Un 'CATALOG' no tiene referencia a un objecto en BD.");
            }
            
            mTmp=new MemoryTable(eTmp.getAttributeValue("object"));
            if( eTmp.getAttribute("text")!=null && !eTmp.getAttributeValue("text").isEmpty() ){
                mTmp.setTexto(eTmp.getAttributeValue("text"));
            }
            if( eTmp.getAttribute("memory")!=null && (eTmp.getAttributeValue("memory").equalsIgnoreCase("yes") ||
                    eTmp.getAttributeValue("memory").equalsIgnoreCase("true") || eTmp.getAttributeValue("memory").equals("1")) ){
                mTmp.setEnMemoria(true);
            }
            
            //recupera los items del catalogo
            lTmp2=eTmp.getChildren("item");
            for(Element eTmp2: lTmp2){
                if( eTmp2.getAttribute("object")==null || eTmp2.getAttributeValue("object").isEmpty() ){
                    throw new RuntimeException("Un 'ITEM' no tiene referencia a un objecto en BD.");
                }

                cTmp=new MemoryColumn(eTmp2.getAttributeValue("object"));
                if( eTmp2.getAttribute("text")!=null && !eTmp2.getAttributeValue("text").isEmpty() ){
                    cTmp.setTexto(eTmp2.getAttributeValue("text"));
                }
                if( eTmp2.getAttribute("key")!=null && (eTmp2.getAttributeValue("key").equalsIgnoreCase("yes") ||
                        eTmp2.getAttributeValue("key").equalsIgnoreCase("true") || eTmp2.getAttributeValue("key").equals("1")) ){
                    mTmp.agregaLlave(eTmp2.getAttributeValue("object"));
                    cTmp.setLlave(true);
                }
                if( !cTmp.isLlave() && eTmp2.getAttribute("visibleInList")!=null && (eTmp2.getAttributeValue("visibleInList").equalsIgnoreCase("no") ||
                        eTmp2.getAttributeValue("visibleInList").equalsIgnoreCase("false") || eTmp2.getAttributeValue("visibleInList").equals("0")) ){
                    cTmp.setVisible(false);
                }
                if( !cTmp.isLlave() && eTmp2.getAttribute("unique")!=null  && eTmp2.getAttributeValue("unique").equalsIgnoreCase("yes")!=false || 
                        (eTmp2.getAttributeValue("unique").equalsIgnoreCase("true")!=false || eTmp2.getAttributeValue("unique").equals("1")!=false) ){
                    cTmp.setUnico(true);
       
                }
                if( !cTmp.isLlave() && eTmp2.getAttribute("catchable")!=null && (eTmp2.getAttributeValue("catchable").equalsIgnoreCase("no") ||
                        eTmp2.getAttributeValue("catchable").equalsIgnoreCase("false") || eTmp2.getAttributeValue("catchable").equals("0")) ){
                    cTmp.setCapturable(false);
                }
                if( eTmp2.getAttribute("order")!=null && eTmp2.getAttributeValue("order").equalsIgnoreCase("asc") ){
                    cTmp.setOrdenacion(TypeOrder.ASC);
                }
                else if( eTmp2.getAttribute("order")!=null && eTmp2.getAttributeValue("order").equalsIgnoreCase("desc") ){
                    cTmp.setOrdenacion(TypeOrder.DESC);
                }
                if( eTmp2.getAttribute("activityControl")!=null && (eTmp2.getAttributeValue("activityControl").equalsIgnoreCase("yes") ||
                        eTmp2.getAttributeValue("activityControl").equalsIgnoreCase("true") || eTmp2.getAttributeValue("activityControl").equals("1")) ){
                    cTmp.setActividad(true);
                }
                if( eTmp2.getAttribute("reference")!=null && !eTmp2.getAttributeValue("reference").isEmpty() ){
                    cTmp.setReferencia(eTmp2.getAttributeValue("reference"));       
                }
                
                mTmp.agregaColumna(cTmp);
            }

            mc.addTable(mTmp);
        }
        
        return mc;
    }        
    
    private void preparaLogger(String raiz){
        Properties config=new Properties();
        
        config.setProperty("log4j.logger."+raiz, (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.org.apache.ibatis", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.java.sql", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.logger.neoAtlantis", (debug? "DEBUG": "ERROR")+", stdout");
        config.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        config.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        config.setProperty("log4j.appender.stdout.layout.ConversionPattern", "[%5p][%d]["+app+"][%c] %m%n");
        
        config.setProperty("log4j.rootLogger", "ERROR, fileout");
        config.setProperty("log4j.appender.fileout", "org.apache.log4j.RollingFileAppender");
        config.setProperty("log4j.appender.fileout.File", this.rutaLog+this.app.replaceAll(" ", "") +".log");
        config.setProperty("log4j.appender.fileout.MaxFileSize", "500000");
        config.setProperty("log4j.appender.fileout.MaxBackupIndex", "100");
        config.setProperty("log4j.appender.fileout.filter.fil1", "org.apache.log4j.varia.LevelRangeFilter");
        config.setProperty("log4j.appender.fileout.filter.fil1.LevelMin", "ERROR");
        config.setProperty("log4j.appender.fileout.filter.fil1.LevelMax", "FATAL");
        config.setProperty("log4j.appender.fileout.layout", "org.apache.log4j.PatternLayout");
        config.setProperty("log4j.appender.fileout.layout.ConversionPattern", "[%5p][%d]["+app+"][%c] %m%n");

        System.out.println("INFO: configig log4j: "+config);
        
        PropertyConfigurator.configure(config);
    }
    
    //--------------------------------------------------------------------------
    
    private static OperatingSystem getSistema(String cabecera){
        if( cabecera!=null && cabecera.toLowerCase().indexOf("windows ce")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows phone")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows mobile")!=-1 ){
            return OperatingSystem.WINDOWS_MOBILE;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("windows")!=-1 ){
            return OperatingSystem.WINDOWS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("linux")!=-1 ){
            return OperatingSystem.LINUX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("iphone")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("ipod")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("ipad")!=-1 ){
            return OperatingSystem.IOS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("mac os")!=-1 ){
            return OperatingSystem.MAC;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("android")!=-1 ){
            return OperatingSystem.ANDROID;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("hpux")!=-1 ){
            return OperatingSystem.HP_UX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("sunos")!=-1 ){
            return OperatingSystem.SOLARIS;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("playbook")!=-1 ){
            return OperatingSystem.PLAYBOOK;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("blackberry")!=-1 ){
            return OperatingSystem.BLACKBERRY;
        }
        
        return OperatingSystem.OTRO;
    }

    private static Browser getNavegador(String cabecera){
        if( cabecera!=null && cabecera.toLowerCase().indexOf("firefox")!=-1 ){
            return Browser.FIREFOX;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("safari")!=-1 ){
            return Browser.SAFARI;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("opera")!=-1 ){
            return Browser.OPERA;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("netscape")!=-1 ){
            return Browser.OPERA;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("msie")!=-1 ){
            return Browser.INTERNET_EXPLORER;
        }
        else if( cabecera!=null && cabecera.toLowerCase().indexOf("chrome")!=-1 ){
            return Browser.GOOGLE_CHROME;
        }
        
        return Browser.OTRO;
    }
    
    
    
}
