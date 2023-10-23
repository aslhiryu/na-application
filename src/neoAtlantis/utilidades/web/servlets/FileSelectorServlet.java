package neoAtlantis.utilidades.web.servlets;

import java.io.*;
import java.net.URLEncoder;
import javax.servlet.http.*;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class FileSelectorServlet extends HttpServlet{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

        try{
            OutputStream out=response.getOutputStream();
            StringBuffer sb=new StringBuffer("");
            File fTmp=new File( request.getParameter("raiz") );

            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>File Selector</title>\n");
            sb.append("<link type='text/css' rel='stylesheet' href='selector.css' />\n");
            sb.append("<script type='text/javascript'>//alert( window.opener.document.getElementById('selectorFile') );\n");
            sb.append("function selecciona(){\n");
            sb.append("  var lis=document.getElementById('raiz');\n");
            sb.append("  if( lis!=null && lis.selectedIndex>=0){\n");
            sb.append("    if(lis.options[lis.selectedIndex].value.substring(0, 2)=='f-'){\n");
            sb.append("      var obj1=window.opener.document.getElementById('selectorFile');\n");
            sb.append("      if( obj1!=null ){\n");
            sb.append("        obj1.value='").append(fTmp.getCanonicalPath().replace('\\','/')).append("/'+lis.options[lis.selectedIndex].value.substring(2);\n");
            sb.append("      }\n");
            sb.append("      window.close();\n");
            sb.append("    }\n");
            sb.append("    else if(lis.options[lis.selectedIndex].value.substring(0, 2)=='d-'){\n");
            sb.append("      location.href='?raiz=").append(URLEncoder.encode(fTmp.getCanonicalPath(), "UTF-8")).append("/'+lis.options[lis.selectedIndex].value.substring(2);\n");
            sb.append("    }\n");
            sb.append("  }\n");
            sb.append("}\n");
            sb.append("</script>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<table border=0 align='center'>\n");
            sb.append("<tr>\n");
            sb.append("<td class='selector_titulo'>Selecciona un Archivo</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<select name='raiz' id='raiz' class='selector_lista' size=20>\n");
            if( fTmp.getParentFile()!=null ){
                sb.append("<option value='d-..'>..</option>\n");
            }
            for(File f: fTmp.listFiles()){
                sb.append("<option value='").append(f.isDirectory()? "d": "f").append("-").append(f.getName()).append("'>").append(f.getName()).append("</option>\n");
            }
            sb.append("</select>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td align='right'><input type='button' value='Seleccionar' class='selector_boton' onclick='selecciona()' /></td>\n");
            sb.append("</tr>\n");
            sb.append("</table>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");

            out.write(sb.toString().getBytes("UTF-8"));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        this.doGet(request, response);
    }

}
