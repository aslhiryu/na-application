package neoAtlantis.utilidades.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neoAtlantis.utilidades.apps.catalogs.MessageCatalog;
import neoAtlantis.utilidades.apps.catalogs.MessageType;
import neoAtlantis.utilidades.apps.escuchadores.AppListener;
import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameter;
import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameters;
import neoAtlantis.utilidades.apps.parameters.objects.ParameterType;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ParametersAdminServlet extends HttpServlet {
    static final Logger LOGGER = Logger.getLogger(ParametersAdminServlet.class);
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        OutputStream out=response.getOutputStream();

        //recupero el objeto de parametros
        MemoryParameters mp=(MemoryParameters)request.getServletContext().getAttribute(AppListener.APP_PARAMETROS);

        if( mp==null ){
            out.write(("<font color='red'><b>No se tienen Parametros a administrar.</b></font>\n").getBytes());
            LOGGER.debug("No existen parametros en memoria.");
        }
        else{
            LOGGER.debug("Accion generada: "+request.getParameter("accion"));
            if( request.getParameter("accion")!=null && request.getParameter("accion").equals("editParameter") && request.getParameter("param")!=null && !request.getParameter("param").isEmpty() ){
                out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), null).getBytes());
                LOGGER.debug("Despliega edicion del parametro: "+request.getParameter("param"));
            }
            else if( request.getParameter("accion")!=null && request.getParameter("accion").equals("modifyParam") ){
                String cTmp=validaCampo(request.getParameter("value"), request.getParameter("tipo"));
                if( cTmp==null ){
                    try{
                        mp.updateParam(request.getParameter("param"), request.getParameter("value"));
                        out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mp, new MessageCatalog(MessageType.OK, "Parametro modificado")).getBytes());
                    }
                    catch(Exception ex){
                        out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), new MessageCatalog(ex.getMessage())).getBytes());
                    }
                }
                else{
                    out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), new MessageCatalog(cTmp)).getBytes());
                }
            }
            else{
                out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mp, null).getBytes());
                LOGGER.debug("Despliega administración de parametros.");
            }
        }
    }

    //--------------------------------------------------------------------------
    
    public static String generaTablaEdicion(String action, MemoryParameters mp, String param, MessageCatalog msg){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<table border=0 class='NA_parametros_tablaPrincipal'>\n");
        sb.append("<form name='NA_parametros_formaParametros' action='").append(action).append("' method='post'>\n");
        sb.append("<input type='hidden' name='accion' value='modifyParam'>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_titulo'>Edición de parametro</td>\n");
        sb.append("</tr>\n");
        sb.append(generaMensages(msg));
        MemoryParameter p=mp.getParameter(param);
        if(p!=null){
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<table border=0 class='NA_parametros_tablaCaptura'>\n");
            sb.append("<tr>\n");
            sb.append("<td class='NA_param'>").append(param).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class='NA_dato'>").append(p.getDescripcion()).append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class='NA_dato'><input name='value' size=50 maxlength=255 value='").append(p.getValor()).append("' /></td>\n");
            sb.append("</tr>\n");
            sb.append("</table>\n");
            sb.append("<input type='hidden' name='tipo' value='").append(p.getTipo()).append("' />\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
        }
        sb.append("<tr>\n");
        sb.append("<td class='NA_boton_modifica'>");
        sb.append("<input type='button' value='Modificar' onclick='this.form.submit();' />");
        sb.append("<input type='button' value='Cancelar' onclick='this.form.accion.value=\"\";this.form.submit();' />");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("<input type='hidden' name='param' value='").append(param).append("'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");        
        
        return sb.toString();
    }

    public static String generaTablaAdmin(String action, MemoryParameters mp, MessageCatalog msg){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<table border=0 class='NA_parametros_tablaPrincipal'>\n");
        sb.append("<form name='NA_parametros_formaParametros' action='").append(action).append("'>\n");
        sb.append("<input type='hidden' name='accion' value=''>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_titulo'>Administraci&oacute;n de parametros</td>\n");
        sb.append("</tr>\n");
        sb.append(generaMensages(msg));
        sb.append("<tr>\n");
        sb.append("<td>\n");
        sb.append("<table border=0 class='NA_parametros_tablaDatos'>\n");
        sb.append("<tr class='NA_titulo'>\n");
        sb.append("<td>Parametro</td><td>Valor</td><td>Descripción</td><td>Tipo</td><td>Acciones</td>\n");
        sb.append("</tr>\n");
        int i=0;
        MemoryParameter p;
        for(String k: mp.getParametersKeys()){
            p=mp.getParameter(k);
            sb.append("<tr class='NA_renglon_tipo").append((i%2)+1).append("'>\n");
            sb.append("<td>").append(k).append("</td>\n");
            sb.append("<td>").append(p.getValor()).append("</td>\n");
            sb.append("<td>").append(p.getDescripcion()).append("</td>\n");
            sb.append("<td>").append(p.getTipo()).append("</td>\n");
            sb.append("<td><a href='#' class='NA_boton_editar' onclick='document.NA_parametros_formaParametros.accion.value=\"editParameter\";document.NA_parametros_formaParametros.param.value=\"").append(k).append("\";document.NA_parametros_formaParametros.submit();' title='Editar'>&nbsp;</a></td>\n");
            sb.append("</tr>\n");
            i++;
        }
        sb.append("</table>\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("<input type='hidden' name='param' value='0'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");

        return sb.toString();
    }

    public static String validaCampo(String valor, String tipo){
        if(tipo!=null && tipo.equalsIgnoreCase("numerico")){
            return validaCampo(valor, ParameterType.NUMERICO);
        }
        else if(tipo!=null && tipo.equalsIgnoreCase("fecha")){
            return validaCampo(valor, ParameterType.FECHA);
        }
        else if(tipo!=null && tipo.equalsIgnoreCase("boleano")){
            return validaCampo(valor, ParameterType.BOLEANO);
        }
        else{
            return validaCampo(valor, ParameterType.CADENA);
        }
    }
    
    public static String validaCampo(String valor, ParameterType tipo){
        //valida que exista un valor
        if( valor==null){
            return "<ul><li>No existe el valor</li></ul>";
        }
        else{
            //valida que no este vacio el valor
            if( valor.isEmpty() ){
                return "<ul><li>Se debe proporcionar un valor</li></ul>";
            }
            else{
                //valida si es de tipo numerico
                if(tipo==ParameterType.NUMERICO){
                    try{
                        Double.parseDouble(valor);
                    }
                    catch(Exception ex){
                        return "<ul><li>Se debe proporcionar un valor númerico</li></ul>";
                    }
                }
                //valida si es de tipo boleano
                else if(tipo==ParameterType.BOLEANO){
                    if( !valor.equalsIgnoreCase("true") && !valor.equalsIgnoreCase("t") && !valor.equalsIgnoreCase("false") && !valor.equalsIgnoreCase("f") &&
                            !valor.equalsIgnoreCase("si") && !valor.equalsIgnoreCase("s") && !valor.equalsIgnoreCase("no") && !valor.equalsIgnoreCase("n") &&
                            !valor.equalsIgnoreCase("yes") && !valor.equalsIgnoreCase("y") && !valor.equalsIgnoreCase("verdadero") && !valor.equalsIgnoreCase("v") &&
                            !valor.equalsIgnoreCase("falso") && !valor.equals("1") && !valor.equals("0") ){
                        return "<ul><li>Se debe proporcionar un valor boleano</li></ul>";
                    }
                }
                //valida si es de tipo fecha
                else if(tipo==ParameterType.FECHA){
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                    try{
                        sdf.parse(valor);
                    }
                    catch(Exception ex){
                        return "<ul><li>Se debe proporcionar una fecha (dd/mm/aaaa)</li></ul>";
                    }
                }
            }
        }
        
        return null;
    }

    //--------------------------------------------------------------------------
    
    private static String generaMensages(MessageCatalog msg){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<tr>\n");
        sb.append("<td>&nbsp;</td>\n");
        sb.append("</tr>\n");
        if( msg!=null ){
            sb.append("<tr>\n");
            sb.append("<td><span class='").append(msg.getTipo()==MessageType.ERROR? "NA_mensaje_error": "NA_mensaje_ok").append("'>").append(msg.getMensaje()).append("</span></td>\n");
            sb.append("</tr>\n");
        }
        
        return sb.toString();
    }
}
