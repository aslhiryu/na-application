package neoAtlantis.utilidades.logger.utils;

import java.io.*;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.*;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PageLogVisualizerServlet  extends HttpServlet{
    public static final String LISTA_LOGS="NA_Utils.visualizardor.logs";
    public static final String MAX_LOGS="NA_Utils.visualizardor.max";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        File dir;
        ArrayList<File> archivos=new ArrayList<File>();
        long m=1000l;

        try{
            //cargo el archivo de propiedades
            Properties prop=cargaConfiguracion(this.getServletContext());

            //valida la existencia d ela configuracion
            if( prop==null ){
                response.sendRedirect("configura.jsp");
                return;
            }

            //valida la existencia del archivo
            if( prop.getProperty("file")!=null && prop.getProperty("file").trim().length()>0 ){
                archivos.add(new File(prop.getProperty("file")));
            }
            //si no es por archivo valida la existencia del directorio
            else if( prop.getProperty("dir")!=null && prop.getProperty("dir").trim().length()>0 ){
                dir=new File(prop.getProperty("dir"));

                //recupero los archivos en el directorio
                File[] fs=dir.listFiles();

                for(File f: fs){
                    //genero la lista de archivos disponibles
                    if( f.isFile() && f.canRead() ){
                        archivos.add(f);
                    }
                }
            }

            //carga el valor del max
            try{
                m=Long.parseLong( prop.getProperty("max") );
            }catch(Exception ex2){}

            //carga los datos en session
            request.getSession().setAttribute(LISTA_LOGS, archivos);
            request.getSession().setAttribute(MAX_LOGS, m);

            OutputStream out=response.getOutputStream();
            StringBuffer sb=new StringBuffer("");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Visualizador de Logs</title>\n");
            sb.append("<link type='text/css' rel='stylesheet' href='logs.css' />\n");
            sb.append("<script type='text/javascript'>\n");
            sb.append("function cargaPagina(adicional){\n");
            sb.append("  var frm=document.getElementById('log_frame');\n");
            sb.append("  var lay=document.getElementById('log_titulo');\n");
            sb.append("  var lis=document.getElementById('arcs');\n");
            sb.append("  var num=0;\n\n");
            sb.append("  if( frm!=null ){\n");
            sb.append("    if( lis!=null ){\n");
            sb.append("      num=lis.selectedIndex;\n");
            sb.append("      lay.innerHTML=lis.options[lis.selectedIndex].value;\n");
            sb.append("    };\n");
            sb.append("    frm.src='visualizaArchivo.slt?arc='+num+'&'+(adicional==null? '': adicional);\n");
            sb.append("  }\n");
            sb.append("}\n");
            sb.append("function asignaMax(){\n");
            sb.append("  var caj=document.getElementById('max');\n\n");
            sb.append("  if( caj!=null ){\n");
            sb.append("    cargaPagina('max='+caj.value);\n");
            sb.append("  }\n");
            sb.append("}\n");
            sb.append("</script>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<div id='log_titulo'>").append((archivos.isEmpty()? "[DESCONOCIDO]": archivos.get(0).getName())).append("</div>\n");
            sb.append("<center><iframe id='log_frame' width='99%' height='89%' src='visualizaArchivo.slt?arc=0'></iframe></center>\n");
            sb.append("<div id='log_opciones'>");
            if( archivos.size()>1 ){
                sb.append("<select name='arcs' id='arcs' class='log_caja'>\n");
                for(int i=0; i<archivos.size(); i++){
                    sb.append("  <option value='").append(archivos.get(i).getName()).append("'>").append(archivos.get(i).getName()).append("</option>\n");
                }
                sb.append("</select>\n");
            }
            sb.append("<input type='button' value='").append(archivos.size()>1? "Seleccionar": "Recargar").append("' onclick='cargaPagina();'  class='log_boton' />");
            sb.append(" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Bytes a desplejar<input name='max' id='max' size=5 value='").append(m).append("' class='log_caja' /><input type='button' value='Asignar' onclick='asignaMax()' class='log_boton' />");
            if( prop!=null && prop.getProperty("restart")!=null && prop.getProperty("restart").equalsIgnoreCase("true") ){
                sb.append(" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <input type='button' value='Reiniciar Log' onclick='cargaPagina(\"restart\");' class='log_boton' />");
            }
            sb.append(" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <input type='button' value='Descargar Completo' onclick='cargaPagina(\"descargar\");' class='log_boton' />");
            sb.append("</div>\n");
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

    public static Properties cargaConfiguracion(ServletContext ctx) {
        Properties prop=new Properties();;
        String archivo=ctx.getRealPath("/")+"/WEB-INF/logVisualizer.properties";

        //reviso si existe alguna configuracion especifica
        if( ctx.getInitParameter("configLogVisualizerNA")!=null && ctx.getInitParameter("configLogVisualizerNA").length()>2 ){
            archivo=ctx.getInitParameter("configLogVisualizerNA");
        }

        try{
            prop.load(new FileInputStream(archivo));
        }
        catch(Exception ex){
            return null;
        }

        return prop;
    }
}
