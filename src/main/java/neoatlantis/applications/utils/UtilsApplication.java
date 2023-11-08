package neoatlantis.applications.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import neoatlantis.applications.parameters.interfaces.MemoryParametersLoader;
import neoatlantis.accesscontroller.objects.OperatingSystem;
import neoatlantis.utils.configurations.ClassGenerator;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class UtilsApplication {
    private static final Logger DEBUGER=Logger.getLogger(UtilsApplication.class);
    
    public static String getAppPath(Class clase){
        DEBUGER.debug("Intenta definir la ruta de la aplicación a partir de la clase: "+clase);
        
        String[] pkcs=clase.getPackage().getName().split("\\.");
        String baseOriginal=clase.getResource(clase.getSimpleName()+".class").getFile();
        String baseModificada=baseOriginal;

        DEBUGER.debug("Paquetes considerados: "+pkcs.length);
        DEBUGER.debug("Ruta original: "+baseOriginal);
                
        //remueve los directorios que no sirven
        for(int i=0; i<=(pkcs.length+2); i++){
            baseModificada=baseModificada.substring(0, baseModificada.lastIndexOf('/'));
        }
        
        return baseModificada;
    }
    
    public static String getInitParameter(String raizWeb, String parametro) throws JDOMException, IOException{
        //cargo la información del archivo web.aml
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(raizWeb+"/WEB-INF/web.xml");
        
        //recupero los parametros de inicio
        List<Element> params=doc.getRootElement().getChildren();
        DEBUGER.debug("Parametros: "+params);
        
        for(int i=0; params!=null&&i<params.size(); i++){
            if( params.get(i).getName().equals("context-param") && params.get(i).getChildText("param-name", params.get(i).getNamespace()).equals(parametro)){
                DEBUGER.debug("Parametro encontrado");
                return params.get(i).getChildText("param-value", params.get(i).getNamespace());
            }
        }
        
        return null;
    }
    
    
    //parametros----------------------------------------------------------------
    
    public static MemoryParametersLoader getParametersLoader(String configFile, Map<String,Object> entorno) throws Exception{
        DEBUGER.debug("Intenta generar el ParameterLoader a partir de la configuración: "+configFile+", con entorno: "+entorno);
                
        try{
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(configFile);
            Element eTmp = doc.getRootElement().getChild("parameters");

            DEBUGER.debug("Elemento recuperado: "+eTmp);
            if( eTmp==null ){
                return null;
            }

            Object obj=ClassGenerator.createInstance(eTmp, entorno);
            DEBUGER.debug("MemoryLoader instanciado: "+obj);

            if( obj==null ){
                return null;
            }
            else{
                return (MemoryParametersLoader)obj;
            }   
        }
        catch(Exception ex){
            DEBUGER.error("No se logro cargar el ParameterLoader", ex);
            return null;
        }
    }

    
    
    //moviles-------------------------------------------------------------------
    
    public static boolean isMovil(OperatingSystem os){
        if(os==OperatingSystem.ANDROID || os==OperatingSystem.PLAYBOOK || 
                os==OperatingSystem.BLACKBERRY || os==OperatingSystem.IOS || 
                os==OperatingSystem.WINDOWS_MOBILE) {
            return true;
        }
        
        return false;
    }


}
