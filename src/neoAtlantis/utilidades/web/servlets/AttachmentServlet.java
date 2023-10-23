package neoAtlantis.utilidades.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public abstract class AttachmentServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        response.setContentType(this.getTipoArchivo(request));
        response.setHeader("Content-Disposition", "attachment; filename=" + this.getNombreArchivo(request));
        response.setHeader("Cache-Control","no-cache"); 
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires", 0);
        
        OutputStream out=response.getOutputStream();
        InputStream in=this.getArchivo(request);
        
        int b;
        while( (b=in.read())!=-1 ){
            out.write(b);
        }
        
        out.flush();        
    }

    public abstract InputStream getArchivo(HttpServletRequest request) throws IOException;
    public abstract String getNombreArchivo(HttpServletRequest request);
    public abstract String getTipoArchivo(HttpServletRequest request);
}