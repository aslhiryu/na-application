package neoAtlantis.utilidades.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryColumn;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryTable;
import neoAtlantis.utilidades.apps.catalogs.MessageCatalog;
import neoAtlantis.utilidades.apps.catalogs.MessageType;
import neoAtlantis.utilidades.apps.catalogs.interfaces.CatalogsLoader;
import neoAtlantis.utilidades.apps.catalogs.objects.TypeData;
import neoAtlantis.utilidades.apps.escuchadores.AppListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class CatalogAdminServlet extends HttpServlet {
    static final Logger LOGGER = Logger.getLogger(CatalogAdminServlet.class);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        OutputStream out=response.getOutputStream();

        //recupero el objeto de catalogos
        MemoryCatalogs mc=(MemoryCatalogs)request.getServletContext().getAttribute(AppListener.APP_CATALOGOS);

        if( mc==null ){
            out.write(("<font color='red'><b>No se tienen Catalogos a administrar.</b></font>\n").getBytes());
            LOGGER.debug("No existen catalogos en memoria.");
        }
        else{
            LOGGER.debug("Accion generada: "+request.getParameter("accion"));
            if( request.getParameter("accion")!=null && request.getParameter("accion").equals("selectCatalog") && request.getParameter("catalogo")!=null && !request.getParameter("catalogo").isEmpty() ){
                out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null).getBytes());
                LOGGER.debug("Despliega información del catalogo: "+request.getParameter("catalogo"));
            }
            else if( request.getParameter("accion")!=null && request.getParameter("accion").equals("editCatalog") ){
                out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null, request.getParameter("registro"), mc.getDataById(mc.getCatalog(request.getParameter("catalogo")), request.getParameter("registro"))).getBytes());
                LOGGER.debug("Despliega edicion del catalogo: "+request.getParameter("catalogo"));
            }
            else if( request.getParameter("accion")!=null && request.getParameter("accion").equals("addCatalog") ){
                out.write(generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null, null).getBytes());
                LOGGER.debug("Despliega adicion del catalogo: "+request.getParameter("catalogo"));
            }
            else if( request.getParameter("accion")!=null && request.getParameter("accion").equals("addRegistry") ){
                String cTmp=validaCampos(request.getParameterMap(), mc.getCatalog(request.getParameter("catalogo")));
                if( cTmp==null ){
                    try{
                        mc.addData(mc.getCatalog(request.getParameter("catalogo")), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
                        out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(MessageType.OK, "Registro agregado")).getBytes());
                    }
                    catch(Exception ex){
                        out.write(generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(ex.getMessage()), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo")))).getBytes());
                    }
                }
                else{
                    out.write(generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(cTmp), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo")))).getBytes());
                }
            }
            else if( request.getParameter("accion")!=null && request.getParameter("accion").equals("modifyRegistry") ){
                String cTmp=validaCampos(request.getParameterMap(), mc.getCatalog(request.getParameter("catalogo")));
                if( cTmp==null ){
                    try{
                        mc.updateData(mc.getCatalog(request.getParameter("catalogo")), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));                    
                        out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(MessageType.OK, "Registro modificado")).getBytes());
                    }
                    catch(Exception ex){
                        out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(ex.getMessage()), request.getParameter("registro"), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo")))).getBytes());
                    }
                }
                else{
                    out.write(generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(cTmp), request.getParameter("registro"), convierteParametros(request, mc.getCatalog(request.getParameter("catalogo")))).getBytes());
                }
            }
            else{
                out.write(generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, null, null).getBytes());
                LOGGER.debug("Despliega administración de catalogos.");
            }
        }
                
        out.flush();
        out.close();
        out=null;
    }
    
    //--------------------------------------------------------------------------
    
    public static Map<String,Object> parseaComandos(HttpServletRequest request){
        HashMap<String,Object> data=new HashMap<String,Object>();
        
        if( request.getParameter("accion")!=null ){
            data.put("accion", request.getParameter("accion"));
        }
        else{
            data.put("accion", "");
        }
        
        return data;
    }
    
    public static String validaCampos(Map<String,String[]> datos, MemoryTable t){
        StringBuilder sb=new StringBuilder("");
        String[] d;
        
        if( t!=null){
            for(MemoryColumn c: t.getColumnas()){
                if( !c.isCapturable() ){
                    continue;
                }

                d=datos.get(c.getNombre());
                if(d==null || d.length==0 || d[0]==null || d[0].isEmpty()){
                    sb.append("<li>Falta del valor de '").append(c.getTexto()).append("'</li>");
                }
                else if( c.getTipo()==TypeData.DECIMAL || c.getTipo()==TypeData.ENTERO ){
                    try{
                        Double.parseDouble(d[0]);
                    }
                    catch(Exception ex){
                        sb.append("<li>El valor de '").append(c.getTexto()).append("' debe ser númerico</li>");
                    }
                }
            }
        }
        
        return (sb.length()>0? "<ul>"+sb.toString()+"</ul>": null);
    }
    
    public static Map<String,Object> convierteParametros(HttpServletRequest request, MemoryTable t){
        HashMap<String,Object> d=new HashMap<String,Object>();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

        if(t!=null){
            for(MemoryColumn c: t.getColumnas()){
                if( request.getParameter(c.getNombre())==null ){
                    continue;
                }

                switch(c.getTipo()){
                    case ENTERO:{
                        try{
                            d.put(c.getNombre(), Integer.parseInt(request.getParameter(c.getNombre())));
                        }
                        catch(Exception ex){                                
                        }
                        break;
                    }
                    case DECIMAL:{
                        try{
                            d.put(c.getNombre(), Double.parseDouble(request.getParameter(c.getNombre())));
                        }
                        catch(Exception ex){                                
                        }
                        break;
                    }
                    case FECHA:{
                        try{
                            d.put(c.getNombre(), sdf.parse(request.getParameter(c.getNombre())));
                        }
                        catch(Exception ex){                                
                        }
                        break;
                    }
                    case BOLEANO:{
                        if( request.getParameter(c.getNombre())!=null && (request.getParameter(c.getNombre()).equalsIgnoreCase("true") ||
                                request.getParameter(c.getNombre()).equalsIgnoreCase("yes") || request.getParameter(c.getNombre()).equalsIgnoreCase("si") ||
                                request.getParameter(c.getNombre()).equalsIgnoreCase("t") || request.getParameter(c.getNombre()).equalsIgnoreCase("1")) ){
                            d.put(c.getNombre(), Boolean.TRUE);
                        }                            
                        else{
                            d.put(c.getNombre(), Boolean.FALSE);
                        }
                        break;
                    }
                    default:{
                        if( request.getParameter(c.getNombre())!=null ){
                            d.put(c.getNombre(), request.getParameter(c.getNombre()));
                        }                            
                    }
                }
            }
        }

        return d;
    }
    
    public static String generaTablaEdicion(String action, MemoryCatalogs mc, String cat, MessageCatalog msg, String key, Map<String,Object> data){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<table border=0 class='NA_catalogos_tablaPrincipal'>\n");
        sb.append("<form name='NA_catalogos_formaCatalogos' action='").append(action).append("' method='post'>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_titulo'>Edición de registro</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(generaMensages(msg));
        sb.append(generaFormaCaptura(mc.getCatalog(cat), data, true));
        sb.append("<tr>\n");
        sb.append("<td class='NA_boton_modifica'>");
        sb.append("<input type='button' value='Modificar' onclick='this.form.accion.value=\"modifyRegistry\";this.form.submit();' />");
        sb.append("<input type='button' value='Cancelar' onclick='this.form.accion.value=\"\";this.form.submit();' />");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("<input type='hidden' name='registro' value='").append(key).append("'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");        
        
        return sb.toString();
    }
    
    public static String generaTablaAdicion(String action, MemoryCatalogs mc, String cat, MessageCatalog msg, Map<String,Object> data){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<table border=0 class='NA_catalogos_tablaPrincipal'>\n");
        sb.append("<form name='NA_catalogos_formaCatalogos' action='").append(action).append("' method='post'>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_titulo'>Nuevo registro</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(generaMensages(msg));
        sb.append(generaFormaCaptura(mc.getCatalog(cat), data, false));
        sb.append("<tr>\n");
        sb.append("<td class='NA_boton_nuevo'>");
        sb.append("<input type='button' value='Agregar' onclick='this.form.accion.value=\"addRegistry\";this.form.submit();' />");
        sb.append("<input type='button' value='Cancelar' onclick='this.form.accion.value=\"\";this.form.submit();' />");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");        
        
        return sb.toString();
    }
    
    public static String generaTablaAdmin(String action, MemoryCatalogs mc, String cat, MessageCatalog msg){
        StringBuilder sb=new StringBuilder("");
        List<Object[]> data=null;
        int[] posKey=null;
        String key;
        long regs=0;
        
        sb.append("<table border=0 class='NA_catalogos_tablaPrincipal'>\n");
        sb.append("<form name='NA_catalogos_formaCatalogos' action='").append(action).append("'>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_titulo'>Administraci&oacute;n de cat&aacute;logos</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(generaMensages(msg));
        if(cat!=null && cat.length()>0){
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<table border=0 class='NA_catalogos_tablaDatos'>\n");
            //para los titulos de columnas
            sb.append("<tr class='NA_titulo'>\n");            
            for(MemoryTable t: mc.getCatalogs()){
                if(t.getNombre().equals(cat)){
                    posKey=new int[t.getLlaves().size()];
                    for(int j=0,i=0; j<t.getColumnas().size(); j++){
                        sb.append("<td>").append(t.getColumnas().get(j).getTexto()).append("</td>\n");
                        if( t.getColumnas().get(j).isLlave() ){
                            posKey[i]=j;
                            i++;
                        }
                    }
                    break;
                }
            }
            sb.append("<td>Acciones</td>\n");
            sb.append("</tr>\n");
            
            //recupera info
            data=mc.getData(cat);
            regs=mc.getRecordsCount(cat);
            
            //pinta datos
            for(int i=0; data!=null&&i<data.size(); i++){
                sb.append("<tr class='NA_renglon_tipo").append((i%2)+1).append("'>\n");
                key="";
                for(int j=0; j<data.get(i).length; j++){
                    sb.append("<td>").append(data.get(i)[j]).append("</td>\n");
                    
                    for(int k=0; posKey!=null&&k<posKey.length; k++){
                        if( posKey[k]==j ){
                            if( !key.isEmpty() ){
                                key+=CatalogsLoader.SEPARADOR_LLAVE;
                            }
                            key+=data.get(i)[j];
                        }
                    }
                }
                sb.append("<td><a href='#' class='NA_boton_editar' onclick='document.NA_catalogos_formaCatalogos.accion.value=\"editCatalog\";document.NA_catalogos_formaCatalogos.registro.value=\"").append(key).append("\";document.NA_catalogos_formaCatalogos.submit();' title='Editar'>&nbsp;</a></td>\n");
                sb.append("</tr>\n");
            }
                    
            sb.append("</table>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class='NA_boton_nuevo'><input type='button' value='Nuevo Registro' onclick='this.form.accion.value=\"addCatalog\";this.form.submit();' /></td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td><div class='NA_espacio'></div></td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class='NA_detalle'><span class='NA_num_registros'>").append(regs).append("</span> Registros</td>\n");
            sb.append("</tr>\n");
        }
        sb.append("<input type='hidden' name='registro' value='0'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");
        
        return sb.toString();
    }
    
    public static String despliegaCombo(MemoryCatalogs mc, String cat, String id, String def){
        StringBuilder sb=new StringBuilder("");
        
        if(cat==null){
            sb.append("<font color='red'><b>No se tienen opciones a desplegar.</b></font>\n");
        }
        else{
            sb.append("<select id='").append(id).append("' class='NA_catalogos_comboOpciones'>\n");
            sb.append("<option value='0'>").append(def).append("</option>\n");
            for(String[]cTmp: mc.getDataList(cat)){
                sb.append("<option value='").append(cTmp[0]).append("'>").append(cTmp[1]).append("</option>\n");
            }
            sb.append("</select>\n");
        }
        
        return sb.toString();
    }

    //--------------------------------------------------------------------------
    
    
    private static String generaFormaCaptura(MemoryTable t, Map<String,Object> data, boolean todo){
        StringBuilder sb=new StringBuilder("");

        if(t!=null){
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<table border=0 class='NA_catalogos_tablaCaptura'>\n");
            for(int j=0; j<t.getColumnas().size(); j++){
                if( !todo && !t.getColumnas().get(j).isCapturable() ){
                    continue;
                }

                sb.append("<tr>\n");
                sb.append("<td class='NA_campo'>").append(t.getColumnas().get(j).getTexto()).append("</td>\n");
                sb.append("<td class='NA_dato'>");
                if( todo && !t.getColumnas().get(j).isCapturable() ){
                    sb.append( data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "" );
                    sb.append("<input type='hidden' name='").append(t.getColumnas().get(j).getNombre()).append("' value=\"")
                            .append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "").append("\" />");
                }
                else{
                    switch(t.getColumnas().get(j).getTipo()){
                        case ENTERO:{
                            sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=15 value=\"")
                                    .append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "").append("\" />");
                            break;
                        }
                        case DECIMAL:{
                            sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=20 value=\"")
                                    .append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "").append("\" />");
                            break;
                        }
                        case BOLEANO:{
                            sb.append("<input type='radio' name='").append(t.getColumnas().get(j).getNombre()).append("' value=0 ").append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null&&!((Boolean)data.get(t.getColumnas().get(j).getNombre()))? "checked": "").append(" />No ");
                            sb.append("<input type='radio' name='").append(t.getColumnas().get(j).getNombre()).append("' value=1 ").append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null&&((Boolean)data.get(t.getColumnas().get(j).getNombre()))? "checked": "").append("/>Si ");
                            break;
                        }
                        case FECHA:{
                            sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=10 maxlength=10 value=\"")
                                    .append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "").append("\" />");
                            break;
                        }
                        default:{
                            sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=").append(t.getColumnas().get(j).getTamano()>40? 40: t.getColumnas().get(j).getTamano())
                                    .append(" maxlength=").append(t.getColumnas().get(j).getTamano())
                                    .append(" value=\"").append(data!=null&&data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "").append("\" />");
                        }
                    }
                }
                sb.append("</td>\n");
                sb.append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
        }
        
        return sb.toString();
    }
    
    private static String generaComboCatalogos(MemoryCatalogs mc, String cat){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("<tr>\n");
        sb.append("<td>&nbsp;</td>\n");
        sb.append("</tr>\n");
        sb.append("<tr>\n");
        sb.append("<td class='NA_catalogos_listaCatalogos'>\n");
        sb.append("Cat&aacute;logo a administrar\n");
        sb.append("<input type='hidden' name='accion' value='selectCatalog'>\n");
        sb.append("<select name='catalogo'>\n");
        sb.append("<option value=''>[---  Seleccione un catalogo  ---]</option>\n");
        for(MemoryTable t: mc.getCatalogs()){
            sb.append("<option value='").append(t.getNombre()).append("'");
            if( cat!=null && cat.equals(t.getNombre()) ){
                sb.append(" selected");
            }
            sb.append(">").append(t.getTexto()).append("</option>\n");
        }
        sb.append("</select>\n");
        sb.append("<input type='button' value='Seleccionar' class='NA_boton' onclick='this.form.submit();' />\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        return sb.toString();
    }
    
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
