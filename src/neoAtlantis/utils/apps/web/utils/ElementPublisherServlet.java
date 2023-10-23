package neoAtlantis.utils.apps.web.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ElementPublisherServlet extends HttpServlet {
    private static final Logger DEBUGER = Logger.getLogger(ElementPublisherServlet.class);
    
    public static String SERVLET_CONTEXT_WIDCARD="##SERVLET_CONTEXT##";

    private InputStream file;
    private String name;
    private String type;

    
    
    
    
    // Contructores ------------------------------------------------------------

    public ElementPublisherServlet(InputStream file, String name, String type){
        this.file=file;
        this.name=name;
        this.type=type;
    }
    
    public ElementPublisherServlet(InputStream file, String name){
        this(file, name, null);
    }

    
    
    
    // Metodos publicos---------------------------------------------------------
        
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        int i=0;
        
        DEBUGER.debug("Despliega el recurso: "+this.name);
        try{
            if( this.file!=null && this.name!=null && !this.name.isEmpty() ){
                if( this.type!=null && !this.type.isEmpty()){
                    response.setContentType(this.type);
                }            
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Content-Disposition", "attachment; filename=\""+this.name+"\"");

                int nRead;
                ByteArrayOutputStream buffer=new ByteArrayOutputStream();

                //reviso si es un texto
                if( this.type!=null && this.type.startsWith("text/") ){
                    DEBUGER.debug("Carga un elemento de tipo texto");

                    //si es de tipo texto valido la parte de los comodides
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(this.file));
                    String buf = null;

                    while ((buf = rdr.readLine()) != null) {
                        //DEBUGER.debug("==> "+buf);
                        buffer.write( buf.replaceAll(SERVLET_CONTEXT_WIDCARD, request.getServletContext().getContextPath()).getBytes() );
                        buffer.write( ("\n").getBytes() );
                        i++;
                    }

                    buffer.flush();
                    DEBUGER.debug(i+" lineas revisadas");
                }
                else{
                    DEBUGER.debug("Carga un elemento de tipo binario");
                    byte[] data = new byte[16384];
                    while( (nRead = this.file.read(data, 0, data.length))!=-1 ){
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                }

                this.file.reset();
                response.getOutputStream().write(buffer.toByteArray());
                response.flushBuffer();

                DEBUGER.debug("Se despliega el elemento: "+this.name);
            }        
            else{
                DEBUGER.error("No se puede implementar el recurso '"+this.name+"'.");
            }
        }
        catch(Exception ex){
            DEBUGER.error("No logre carga el recurso: "+this.name, ex);
        }
    }    

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doGet(request, response);
    }
}
