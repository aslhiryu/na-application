package neoAtlantis.utilidades.web;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utilidades.apps.catalogs.MessageCatalog;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class PostOfficeHtmlFormatter {
    private static final Logger LOGGER = Logger.getLogger(PostOfficeHtmlFormatter.class);
    
    public static String formateaBandejaEntrada(HttpServletRequest request, List<Message> regs, MessageCatalog msgError) {
        StringBuilder sb = new StringBuilder("<table border=0 class=\'NA_bandejaEntrada_tablaPrincipal\'>\n");
        
        sb.append("<form name=\'NA_bandejaEntrada_forma\' action=\'").append(request.getContextPath()).append(request.getServletPath()).append("\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Bandeja de Entrada</td>\n");
        sb.append("</tr>\n");
        sb.append(ParametersHtmlFormatter.generaMensages(msgError));
        sb.append(generaTablaDatos(regs));
        sb.append("<tr>\n");
        sb.append("<td><a href=\'#\' class=\'NA_boton_nuevo\' onclick=\'document.NA_bandejaEntrada_forma.accion.value=\"newMessage\";document.NA_bandejaEntrada_forma.submit();\' title=\'Nuevo Mensaje\'>&nbsp;</a></td>\n");
        sb.append("</tr>\n");
        sb.append("<input type=\'hidden\' name=\'mensaje\' value=\'0\'>\n");
        sb.append("<input type=\'hidden\' name=\'accion\' value=\'viewMessages\'>\n");
        if (request.getParameter("NA_user_id") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_id\' value=\'").append(request.getParameter("NA_user_id")).append("\'>\n");
        }
        if (request.getParameter("NA_user_name") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_name\' value=\'").append(request.getParameter("NA_user_name")).append("\'>\n");
        }
        if (request.getParameter("NA_post_office") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_post_office\' value=\'").append(request.getParameter("NA_post_office")).append("\'>\n");
        }
        sb.append("</form>\n");
        sb.append("</table>\n");
        return sb.toString();

    }

    public static String generaTablaDatos(List<Message> regs) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder("");

        sb.append("<tr>\n");
        sb.append("<td>\n");
        sb.append("<table border=0 class=\'NA_bandejaEntrada_tablaDatos\'>\n");
        sb.append("<tr class=\'NA_titulo\'>\n");
        sb.append("<td>Remitente</td>\n");
        sb.append("<td>Titulo</td>\n");
        sb.append("<td>Fecha</td>\n");
        sb.append("<td>Acciones</td>\n");
        sb.append("</tr>\n");
        for (int i = 0; regs != null && i < regs.size(); i++) {
            sb.append("<tr class=\'NA_renglon_tipo").append((i%2)+1).append("\'>\n");
            sb.append("<td>").append(regs.get(i).getRemitente()).append("</td>\n");
            sb.append("<td>").append(regs.get(i).getTitulo()).append("</td>\n");
            sb.append("<td>").append(sdf.format(regs.get(i).getCreacion())).append("</td>\n");
            sb.append("<td><a href=\'#\' class=\'NA_boton_editar\' onclick=\'document.NA_bandejaEntrada_forma.accion.value=\"viewMessage\";document.NA_bandejaEntrada_forma.mensaje.value=\"").append(regs.get(i).getId()).append("\";document.NA_bandejaEntrada_forma.submit();\' title=\'Ver\'>&nbsp;</a></td>\n");
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        return sb.toString();
    }

    public static String generaTablaLectura(HttpServletRequest request, Message mensaje) {
        StringBuilder sb = new StringBuilder("");

        sb.append("<table border=0 class=\'NA_bandejaEntrada_tablaPrincipal\'>\n");
        sb.append("<form name=\'NA_bandejaEntrada_formaMensajes\' action=\'").append(request.getContextPath()).append(request.getServletPath()).append("\' method=\'post\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Lectura de Mensaje</td>\n");
        sb.append("</tr>\n");
        sb.append("<tr>\n");
        sb.append("<td>\n");
        if (mensaje != null) {
            sb.append("<table border=0 class=\'NA_bandejaEntrada_tablaCaptura\'>\n");
            sb.append("<tr>\n");
            sb.append("<td class=\'NA_campo\'>Titulo</td>\n");
            sb.append("<td class=\'NA_dato\'>").append(mensaje.getTitulo()).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class=\'NA_campo\'>Remitente</td>\n");
            sb.append("<td class=\'NA_dato\'>").append(mensaje.getRemitente()).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class=\'NA_campo\'>Fecha de Env&iacute;o</td>\n");
            sb.append("<td class=\'NA_dato\'>").append(mensaje.getCreacion()).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class=\'NA_campo\' valign=\'top\'>Mensaje</td>\n");
            sb.append("<td class=\'NA_dato\'>").append(mensaje.getMensaje()).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<a href=\'").append(request.getContextPath()).append(request.getServletPath()).append("\' class=\'NA_boton_regresar\' > &nbsp;</a>");
            sb.append("</td>\n");
            sb.append("</tr>\n");
            sb.append("</table>\n");

        }
        sb.append("</td>\n");
        sb.append("</tr>\n");
        if (request.getParameter("NA_user_id") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_id\' value=\'").append(request.getParameter("NA_user_id")).append("\'>\n");
        }
        if (request.getParameter("NA_user_name") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_name\' value=\'").append(request.getParameter("NA_user_name")).append("\'>\n");
        }
        if (request.getParameter("NA_post_office") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_post_office\' value=\'").append(request.getParameter("NA_post_office")).append("\'>\n");
        }
        sb.append("</form>\n");
        sb.append("</table>\n");
        
        return sb.toString();
    }

    public static String generaFormaCreacion(HttpServletRequest request, MessageCatalog msgError) {
        StringBuilder sb = new StringBuilder("");

        sb.append("<table border=0 class=\'NA_bandejaEntrada_tablaPrincipal\'>\n");
        sb.append("<form name=\'NA_bandejaEntrada_formaMensajes\' action=\'").append(request.getContextPath()).append(request.getServletPath()).append("\' method=\'post\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Nuevo de Mensaje</td>\n");
        sb.append("</tr>\n");
        sb.append(ParametersHtmlFormatter.generaMensages(msgError));
        sb.append("<tr>\n");
        sb.append("<td>\n");
        sb.append("<table border=0 class=\'NA_bandejaEntrada_tablaCaptura\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_campo\'>Titulo</td>\n");
        if (request.getParameter("subject") != null) {
            sb.append("<input name=\'subject\' size=50 value=\'").append(request.getParameter("subject")).append(" /></td>\n");
        }
        sb.append("</tr>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_campo\'>Destinatario</td>\n");
        if (request.getParameter("to") != null) {
            sb.append("<input name=\'to\' size=50 value=\'").append(request.getParameter("to")).append(" /></td>\n");
        }
        sb.append("</tr>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_campo\' valign=\'top\'>Mensaje</td>\n");
        if (request.getParameter("message") != null) {
            sb.append("<td><textarea name=\'message\' rows=8 cols=80>").append(request.getParameter("message")).append("</textarea></td>\n");
        }
        sb.append("</tr>\n");
        sb.append("<a href=\'").append(request.getContextPath()).append(request.getServletPath()).append("\' class=\'NA_boton_regresar\' > &nbsp;</a>");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("</table>\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        if (request.getParameter("NA_user_id") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_id\' value=\'").append(request.getParameter("NA_user_id")).append("\'>\n");
        }
        if (request.getParameter("NA_user_name") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_user_name\' value=\'").append(request.getParameter("NA_user_name")).append("\'>\n");
        }
        if (request.getParameter("NA_post_office") != null) {
            sb.append("<input type=\'hidden\' name=\'NA_post_office\' value=\'").append(request.getParameter("NA_post_office")).append("\'>\n");
        }
        sb.append("<input type=\'hidden\' name=\'accion\' value=\'saveMessage\'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");
        return sb.toString();

    }

    public static String validaCampos(Map<String,String[]> datos) {
        String[] d;
        StringBuilder sb = new StringBuilder("");

        d = (String[]) datos.get("subject");
        if (d == null || d.length == 0 || (d[0] == null || d[0].isEmpty())) {
            sb.append("<li>Falta el Titulo del mensaje.</li>");

        }
        d = (String[]) datos.get("to");
        if (d == null || d.length == 0 || (d[0] == null || d[0].isEmpty())) {
            sb.append("<li>Falta el Destinatario del mensaje.</li>");

        }
        d = (String[]) datos.get("message");
        if (d == null || d.length == 0 || (d[0] == null || d[0].isEmpty())) {
            sb.append("<li>Falta el Cuerpo del mensaje.</li>");

        }
        d = (String[]) datos.get("NA_user_name");
        if (d == null || d.length == 0 || (d[0] == null || d[0].isEmpty())) {
            sb.append("<li>Falta el Remitente del mensaje.</li>");

        }
        
        if (sb.length() > 0) {
            return "<ul>"+sb.toString()+"</ul>";
        } else {
            return null;
        }
    }
}
