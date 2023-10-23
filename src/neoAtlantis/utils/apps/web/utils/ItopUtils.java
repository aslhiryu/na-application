package neoAtlantis.utils.apps.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utils.apps.web.filters.ItopIncidentFilter;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ItopUtils {
    private ItopUtils(){        
    }
    
    public static Map<String,Object> loadReportConfiguration(ServletContext context){
        Map<String,Object> config=new HashMap();
        boolean report=false;
        
        //recupero los valores establecido a nivel de MV
        RuntimeMXBean rt=ManagementFactory.getRuntimeMXBean();
        for(String ct: rt.getInputArguments()){
            //valido si se dbe reportar al itop
            if( ct.equals("-Dna.itop.report") ){
                report=true;
            }
            //recupero la ruta para reportar
            else if( ct.contains("-Dna.itop.url=") ){
                try{
                    config.put(ItopIncidentFilter.ITOP_URL, new URL(ct.substring(ct.indexOf('=')+1)));
                }
                catch(Exception ex){
                    config.put(ItopIncidentFilter.ITOP_URL, null);
                }
            }
            //recupero el usuario para reportar
            else if( ct.contains("-Dna.itop.user=") ){
                try{
                    config.put(ItopIncidentFilter.ITOP_USER, ct.substring(ct.indexOf('=')+1));
                    System.out.println("INFO-NA: Detecta el usuario de iTop: "+config.get(ItopIncidentFilter.ITOP_USER));
                }
                catch(Exception ex){
                    config.put(ItopIncidentFilter.ITOP_USER, null);
                }
            }
            //recupero la ruta para reportar
            else if( ct.contains("-Dna.itop.pass=") ){
                try{
                    config.put(ItopIncidentFilter.ITOP_PASS, ct.substring(ct.indexOf('=')+1));
                    System.out.println("INFO-NA: Detecta la contraseña de iTop: ******");
                }
                catch(Exception ex){
                    config.put(ItopIncidentFilter.ITOP_PASS, null);
                }
            }
        }        

        //recupero los valores establecidos para la aplicacion
        try{
            //recupero el Id del servicio
            config.put(ItopIncidentFilter.SERVICE_ID, Integer.parseInt(context.getInitParameter("ServiceItopNA")));
            System.out.println("INFO-NA: Detecta el id del servicio de iTop: "+config.get(ItopIncidentFilter.SERVICE_ID));
        }
        catch(Exception ex){
            report=false;
            System.out.println("INFO-NA: La variable 'ServiceItopNA' no esta establecida o tiene un valor no valido");
        }
        try{
            //recupero el Id de la persona que reporta
            config.put(ItopIncidentFilter.CALLER_ID, Integer.parseInt(context.getInitParameter("CallerItopNA")));
            System.out.println("INFO-NA: Detecta el id de la persona que reporta a iTop: "+config.get(ItopIncidentFilter.CALLER_ID));
        }
        catch(Exception ex){
            report=false;
            System.out.println("INFO-NA: La variable 'CallerItopNA' no esta establecida o tiene un valor no valido");
        }
        try{
            //recupero el Id de la organizacion que reporta
            config.put(ItopIncidentFilter.ORG_ID, Integer.parseInt(context.getInitParameter("OrganizationItopNA")));
            System.out.println("INFO-NA: Detecta el id de la organizacion que reporta a iTop: "+config.get(ItopIncidentFilter.ORG_ID));
        }
        catch(Exception ex){
            report=false;
            System.out.println("INFO-NA: La variable 'OrganizationItopNA' no esta establecida o tiene un valor no valido");
        }
        try{
            //recupero el Id del EC que reporta
            config.put(ItopIncidentFilter.CI_ID, Integer.parseInt(context.getInitParameter("CiItopNA")));
            System.out.println("INFO-NA: Detecta el id del EC que reporta a iTop: "+config.get(ItopIncidentFilter.CI_ID));
        }
        catch(Exception ex){
            config.put(ItopIncidentFilter.CI_ID, 0);
        }
        
        //realiza la validacion de variables de JVM
        if(report){
            if(!config.containsKey(ItopIncidentFilter.ITOP_URL) || config.get(ItopIncidentFilter.ITOP_URL)==null){
                System.out.println("INFO-NA: El argumento 'na.itop.url' no esta establecido o tiene un valor no valido");
                report=false;
            }
            if(!config.containsKey(ItopIncidentFilter.ITOP_USER) || config.get(ItopIncidentFilter.ITOP_USER)==null){
                System.out.println("INFO-NA: El argumento 'na.itop.user' no esta establecido o tiene un valor no valido");
                report=false;
            }
            if(!config.containsKey(ItopIncidentFilter.ITOP_PASS) || config.get(ItopIncidentFilter.ITOP_PASS)==null){
                System.out.println("INFO-NA: El argumento 'na.itop.pass' no esta establecido o tiene un valor no valido");
                report=false;
            }
            else{
                System.out.println("INFO-NA: Se inician los reportes de excepciones a la herramienta iTop en: "+config.get(ItopIncidentFilter.ITOP_URL));
            }
        }
        
        config.put(ItopIncidentFilter.REPORT_ITOP, report);
        return config;
    }
    
    public static String reportErrorToItop(Map<String,Object> config, HttpServletRequest request, Throwable error){
        //valido si se debe reportar el error
        if(config!=null && config.containsKey(ItopIncidentFilter.REPORT_ITOP) && ((Boolean)config.get(ItopIncidentFilter.REPORT_ITOP))){
            try{
                System.out.println("INFO-NA: Se reporta error a la herramienta iTOP");

                QueryString builder=new QueryString("auth_user", (String)config.get(ItopIncidentFilter.ITOP_USER));
                builder.add("auth_pwd", (String)config.get(ItopIncidentFilter.ITOP_PASS));
                builder.add("json_data", createiTopMessage((HttpServletRequest)request, error, (Integer)config.get(ItopIncidentFilter.ORG_ID), (Integer)config.get(ItopIncidentFilter.CALLER_ID), (Integer)config.get(ItopIncidentFilter.SERVICE_ID), (Integer)config.get(ItopIncidentFilter.CI_ID)));

                byte[] req=builder.getQuery().getBytes();
                URL url=new URL(((URL)config.get(ItopIncidentFilter.ITOP_URL)).toString()+"/webservices/rest.php?version=1.3");
                HttpURLConnection con=(HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("POST", url.toString()+" HTTP/1.1");
                con.setFixedLengthStreamingMode(req.length);
                con.setRequestProperty("Content-Length", ""+req.length);
                con.setRequestProperty("Host", url.getHost()+":"+url.getPort());
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                //envia la peticion
                DataOutputStream outRequest = new DataOutputStream(con.getOutputStream());
                outRequest.write(req);
                outRequest.close();

                //recupero la respuesta
                InputStream inResponse;
                if( con.getResponseCode()==200 ){
                    inResponse = con.getInputStream();
                }
                else{
                    inResponse= con.getErrorStream();
                }

                //vacio la respuesta en un arreglo
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                int bTmp;
                while(inResponse!=null &&(bTmp=inResponse.read())!=-1){
                    bos.write(bTmp);
                }
                req=bos.toByteArray();
                System.out.println("INFO-NA: Error reportado: "+(new String(req)));
                return new String(req);
            }
            catch(Exception ex1){
                System.out.println("INFO-NA: No se logro reportar el error en iTop: "+ex1.getMessage());
            }
        }
        else{
            System.out.println("INFO-NA: Se detecta error pero no se reporta a la herramienta iTop, por esta inhabilitada la opción");
        }
        
        return null;
    }
    
    public static String createiTopMessage(HttpServletRequest request, Throwable error, int organizationId, int callerId, int serviceId, int ciId){
        StringBuilder sb=new StringBuilder();
        StringBuilder detalle=new StringBuilder();
        CharArrayWriter cw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(cw,true);
        Enumeration<String> eTmp;
        String cTmp;
        
        //genera la información de la peticion
        detalle.append("<b>Error detectado en: ").append(request.getRequestURI()).append("</b><br /><br />");
        detalle.append("<b>Información de la cabecera:</b><br>");
        eTmp=request.getHeaderNames();
        while( eTmp.hasMoreElements() ){
            cTmp=eTmp.nextElement();
            detalle.append("<b>").append(cTmp).append("</b>=").append(request.getHeader(cTmp)).append("<br />");
        }
        if( request.getCookies()!=null ){
            detalle.append("<br />");
            detalle.append("<b>Información de las cookies:</b><br>");
            for(Cookie co: request.getCookies()){
                detalle.append("<b>").append(co.getName()).append("</b>=").append(co.getValue()).append("<br />");
            }
        }
        detalle.append("<br />");
        detalle.append("<b>Parametros de la petición:</b><br />");
        eTmp=request.getParameterNames();
        while( eTmp.hasMoreElements() ){
            cTmp=eTmp.nextElement();
            detalle.append("<b>").append(cTmp).append("</b>=").append(request.getParameter(cTmp)).append("<br />");
        }
        detalle.append("<br />");
        detalle.append("<b>Atributos de la petición:</b><br />");
        eTmp=request.getAttributeNames();
        while( eTmp.hasMoreElements() ){
            cTmp=eTmp.nextElement();
            detalle.append("<b>").append(cTmp).append("</b>=").append(request.getAttribute(cTmp)).append("<br />");
        }
        detalle.append("<br />");
        detalle.append("<b>Atributos de la sessión:</b><br />");
        eTmp=request.getSession().getAttributeNames();
        while( eTmp.hasMoreElements() ){
            cTmp=eTmp.nextElement();
            detalle.append("<b>").append(cTmp).append("</b>=").append(request.getSession().getAttribute(cTmp)).append("<br />");
        }
        //genera la información del error
        error.printStackTrace(pw);
        detalle.append("<br />");
        detalle.append("<b>Error:</b><br />");
        detalle.append(cw.toString());

        sb.append("{")
                .append("\"operation\": \"core/create\",")
                .append("\"comment\": \"Detección de error en aplicativo\",")
                .append("\"class\": \"Incident\",")
                .append("\"output_fields\": \"id, friendlyname\",")
                .append("\"fields\": {")
                .append("\"org_id\": ").append(organizationId).append(",")
                .append("\"caller_id\": ").append(callerId).append(",")
                .append("\"title\": \"Detección de incidente en aplicativo\",")
                .append("\"description\": \"").append(detalle.toString().replaceAll("\"", "'").replaceAll("\n", "<br />").replaceAll("\r", "").replaceAll("\t", " ")).append("\",")
                .append("\"impact\": 3,")
                .append("\"siteAttention\": 2,") //temporal, remover cuando no se utilice
                .append("\"origin\": \"monitoring\",")
                .append("\"service_id\": ").append(serviceId).append(",")
                .append("\"urgency\": 3");
        if(ciId>0){
            sb.append("\"functionalcis_list\": [{")
                    .append("\"functionalci_id\": ").append(ciId)
                    .append("}]");
        }
        sb.append("}")
                .append("}");
        
        return sb.toString();
    }    
}




class QueryString {
    private String query = "";

    public QueryString(String name, String value) {
        encode(name, value);
    }

    public void add(String name, String value) {
        query += "&";
        encode(name, value);
    }

    private void encode(String name, String value) {
        try {
            query += URLEncoder.encode(name, "UTF-8");
            query += "=";
            query += URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Broken VM does not support UTF-8");
        }
    }

    public String getQuery() {
        return query;
    }

    public String toString() {
        return getQuery();
    }
}
