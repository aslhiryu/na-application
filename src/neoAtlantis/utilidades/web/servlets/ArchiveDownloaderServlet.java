package neoAtlantis.utilidades.web.servlets;

import java.io.*;
import javax.servlet.http.*;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ArchiveDownloaderServlet  extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        File fTmp=new File(request.getParameter("file"));
        int c;
        
        try{
            OutputStream out=response.getOutputStream();
            FileInputStream fis;

            if( fTmp!=null && fTmp.exists() && fTmp.canRead() && fTmp.isFile() ){
                fis=new FileInputStream(fTmp);

                //response.setContentType ("application/pdf");
                response.setHeader ("Content-Disposition", "attachment; filename=\""+fTmp.getName()+"\"");

                while( (c=fis.read())!=-1 ){
                    out.write(c);
                }

                out.flush();
                fis.close();
            }
            else{
                response.setContentType("text/html");
                out.write(("<font color='red'>No existe el archivo.</font>").getBytes("UTF-8"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        this.doGet(request, response);
    }
}
