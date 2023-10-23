package neoAtlantis.utilidades.web;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utilidades.apps.catalogs.MessageCatalog;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryColumn;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryTable;
import neoAtlantis.utilidades.apps.catalogs.objects.TypeData;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class CatalogsHtmlFormatter {

    public static Map<String, Object> parseaComandos(HttpServletRequest request) {
        HashMap<String, Object> data=new HashMap<String, Object>();

        if (request.getParameter("accion") != null) {
            data.put("accion", request.getParameter("accion"));
        } else {
            data.put("accion", "");
        }
        
        return data;
    }

    public static String validaCampos(Map<String,String[]> datos, MemoryTable t){
        String[] d;
        StringBuilder sb = new StringBuilder("");

        if (t != null) {
            for(MemoryColumn c: t.getColumnas()){
                if (!c.isCapturable()) {
                    continue;
                }
                d = (String[]) datos.get(c.getNombre());
                if (d == null || d.length == 0 || (d[0] == null || d[0].isEmpty() != false)) {
                    sb.append("<li>Falta del valor de \'").append(c.getTexto()).append("\'</li>");
                } 
                else {
                    if (c.getTipo() == TypeData.DECIMAL || c.getTipo() == TypeData.ENTERO) {
                        try {
                            Double.parseDouble(d[0]);
                        } catch (Exception ex) {
                            sb.append("<li>El valor de \'").append(c.getTexto()).append("\' debe ser n\u00famerico</li>");
                        }
                    }
                }
            }
        }
                
        if (sb.length() > 0) {
            return "<ul>"+sb.toString()+"</ul>";
        } else {
            return null;
        }
    }

    public static Map<String, Object> convierteParametros(HttpServletRequest request, MemoryTable t) {
        HashMap<String, Object> d = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (t != null) {
            for (MemoryColumn c : t.getColumnas()) {
                if (request.getParameter(c.getNombre()) == null) {
                    continue;
                }

                switch (c.getTipo()) {
                    case ENTERO: {
                        try {
                            d.put(c.getNombre(), Integer.valueOf(Integer.parseInt(request.getParameter(c.getNombre()))));
                        } catch (Exception ex) {
                        }
                        break;
                    }
                    case DECIMAL: {
                        try {
                            d.put(c.getNombre(), Double.valueOf(Double.parseDouble(request.getParameter(c.getNombre()))));
                        } catch (Exception ex) {
                        }
                        break;
                    }
                    case FECHA: {
                        try {
                            d.put(c.getNombre(), sdf.parse(request.getParameter(c.getNombre())));
                        } catch (Exception ex) {
                        }
                        break;
                    }
                    case BOLEANO: {
                        if (request.getParameter(c.getNombre()) != null) {
                            if (request.getParameter(c.getNombre()).equalsIgnoreCase("true") != false || request.getParameter(c.getNombre()).equalsIgnoreCase("yes") != false || request.getParameter(c.getNombre()).equalsIgnoreCase("si") != false || (request.getParameter(c.getNombre()).equalsIgnoreCase("t") != false || request.getParameter(c.getNombre()).equalsIgnoreCase("1") != false)) {
                                d.put(c.getNombre(), Boolean.TRUE);
                            }
                        } else {
                            d.put(c.getNombre(), Boolean.FALSE);
                        }
                        break;
                    }
                    default: {
                        if (request.getParameter(c.getNombre()) != null) {
                            d.put(c.getNombre(), request.getParameter(c.getNombre()));
                        }
                    }
                }

            }            
        }
        
        return d;
    }

    public static java.lang.String generaTablaEdicion(String action, MemoryCatalogs mc, String cat, MessageCatalog msg, String key, Map<java.lang.String, java.lang.Object> data) {
        StringBuilder sb = new StringBuilder("");

        sb.append("<table border=0 class=\'NA_catalogos_tablaPrincipal\'>\n");
        sb.append("<form name=\'NA_catalogos_formaCatalogos\' action=\'").append(action).append("\' method=\'post\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Edici\u00f3n de registro</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(ParametersHtmlFormatter.generaMensages(msg));
        sb.append(generaFormaCaptura(mc, mc.getCatalog(cat), data, true));
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_boton_modifica\'>");
        sb.append("<input type=\'button\' value=\'Modificar\' onclick=\'this.form.accion.value=\"modifyRegistry\";this.form.submit();\' />");
        sb.append("<input type=\'button\' value=\'Cancelar\' onclick=\'this.form.accion.value=\"\";this.form.submit();\' />");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("<input type=\'hidden\' name=\'registro\' value=\'").append(key).append("\'>\n");
        sb.append("<input type=\'hidden\' name=\'accion\' value=\'selectCatalog\'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");
        
        return sb.toString();
    }

    public static String generaTablaAdicion(String action, MemoryCatalogs mc, String cat, MessageCatalog msg, Map<String,Object> data) {
        StringBuilder sb = new StringBuilder("");

        sb.append("<table border=0 class=\'NA_catalogos_tablaPrincipal\'>\n");
        sb.append("<form name=\'NA_catalogos_formaCatalogos\' action=\'").append(action).append("\' method=\'post\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Nuevo registro</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(ParametersHtmlFormatter.generaMensages(msg));
        sb.append(generaFormaCaptura(mc, mc.getCatalog(cat), data, false));
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_boton_nuevo\'>");
        sb.append("<input type=\'button\' value=\'Agregar\' onclick=\'this.form.accion.value=\"addRegistry\";this.form.submit();\' />");
        sb.append("<input type=\'button\' value=\'Cancelar\' onclick=\'this.form.accion.value=\"\";this.form.submit();\' />");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("<input type=\'hidden\' name=\'accion\' value=\'selectCatalog\'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");

        return sb.toString();
    }

    public static String generaTablaAdmin(String action, MemoryCatalogs mc, String cat, MessageCatalog msg) {
        List data;
        MemoryTable tTmp=null;
        String key;
        StringBuilder sb=new StringBuilder("");

        int i = 0;
        int[] posKey = null;
        long regs;

        sb.append("<table border=0 class=\'NA_catalogos_tablaPrincipal\'>\n");
        sb.append("<form name=\'NA_catalogos_formaCatalogos\' action=\'").append(action).append("\'>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_titulo\'>Administraci&oacute;n de cat&aacute;logos</td>\n");
        sb.append("</tr>\n");
        sb.append(generaComboCatalogos(mc, cat));
        sb.append(ParametersHtmlFormatter.generaMensages(msg));
        if (cat != null && cat.length() > 0) {
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<table border=0 class=\'NA_catalogos_tablaDatos\'>\n");
            sb.append("<tr class=\'NA_titulo\'>\n");
            for(MemoryTable t: mc.getCatalogs()){
                if (t.getNombre().equals(cat) != false) {
                    tTmp = t;
                    posKey = new int[t.getLlaves().size()];
                    i = 0;
                    for(int j=0; j<t.getColumnas().size(); j++){
                        sb.append("<td>").append(t.getColumnas().get(j).getTexto()).append("</td>\n");
                        if (t.getColumnas().get(j).isLlave() != false) {
                            posKey[i] = j;
                            i++;
                        }
                    }
                }
            }
            sb.append("<td>Acciones</td>\n");
            sb.append("</tr>\n");
            
            data = mc.getData(cat);
            regs = mc.getRecordsCount(cat);
            for(i=0; data != null && i < data.size(); i++){
                sb.append("<tr class=\'NA_renglon_tipo").append( (i%2)+1 ).append("\'>\n");
                key = "";
                for(int j=0; j<((Object[])data.get(i)).length; j++){
                    if (tTmp!=null && tTmp.getColumnas().get(j).getReferencia()!=null) {                        
                        sb.append("<td>").append(recuperaDatoReferenciado(mc, tTmp.getColumnas().get(j).getReferencia(), ""+((Object[])data.get(i))[j])).append("</td>\n");
                    } else {
                        sb.append("<td>").append( ((Object[])data.get(i))[j] ).append("</td>\n");
                        for(int k=0; posKey!=null && k<posKey.length; k++){
                            if (posKey[k] == j) {
                                if (!key.isEmpty()) {
                                    key+=" ";
                                }
                                
                                key = key+((Object[])data.get(i))[j];
                            }
                        }
                    }
                }
                sb.append("<td><a href=\'#\' class=\'NA_boton_editar\' onclick=\'document.NA_catalogos_formaCatalogos.accion.value=\"editCatalog\";document.NA_catalogos_formaCatalogos.registro.value=\"").append(key).append("\";document.NA_catalogos_formaCatalogos.submit();\' title=\'Editar\'>&nbsp;</a></td>\n");
                sb.append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("</td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td><a href=\'#\' class=\'NA_boton_nuevo\' onclick=\'document.NA_catalogos_formaCatalogos.accion.value=\"addCatalog\";document.NA_catalogos_formaCatalogos.submit();\' title=\'Nuevo Valor\'>&nbsp;</a></td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td><div class=\'NA_espacio\'></div></td>\n");
            sb.append("</tr>\n");
            sb.append("<tr>\n");
            sb.append("<td class=\'NA_detalle\'><span class=\'NA_num_registros\'>").append(regs).append("</span> Registros</td>\n");
            sb.append("</tr>\n");
        }
        sb.append("<input type=\'hidden\' name=\'accion\' value=\'selectCatalog\'>\n");
        sb.append("<input type=\'hidden\' name=\'registro\' value=\'0\'>\n");
        sb.append("</form>\n");
        sb.append("</table>\n");
        
        return sb.toString();
    }

    public static String despliegaCombo(MemoryCatalogs mc, String cat, String id, String def) {
        return despliegaCombo(mc, cat, id, def, null);
    }

    public static String despliegaCombo(MemoryCatalogs mc, String cat, String id, String def, String val) {
        StringBuilder sb = new StringBuilder("");

        if (cat == null) {
            sb.append("<font color=\'red\'><b>No se tienen opciones a desplegar.</b></font>\n");
        } 
        else {
            sb.append("<select id=\'").append(id).append("\' name=\'").append(id).append("\' class=\'NA_catalogos_comboOpciones\'>\n");
            sb.append("<option value=\'\'>").append(def).append("</option>\n");
            for(String[] cTmp:  mc.getDataList(cat)){
                sb.append("<option value=\'").append(cTmp[0]).append("\'");
                if (val != null && val.equals(cTmp[0]) != false) {
                    sb.append(" selected");
                }
                sb.append(">").append(cTmp[1]).append("</option>\n");
            }
            sb.append("</select>\n");
        }
        
        return sb.toString();
    }

    public static String recuperaDatoReferenciado(MemoryCatalogs mc, String cat, String val) {
        String dato = "";
        
        for(String[] cTmp: mc.getDataList(cat)){
            if (val != null && val.equals(cTmp[0]) != false) {
                dato = cTmp[1];
                break;
            }
        }
        
        return dato;
    }

    private static String generaFormaCaptura(MemoryCatalogs mc, MemoryTable t, Map<String,Object> data, boolean todo) {
        StringBuilder sb = new StringBuilder("");

        if (t != null) {
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<table border=0 class=\'NA_catalogos_tablaCaptura\'>\n");
            
            for(int j=0; j < t.getColumnas().size(); j++){
                if ( todo && t.getColumnas().get(j).isCapturable() ) {
                    sb.append("<tr>\n");
                    sb.append("<td class=\'NA_campo\'>").append(t.getColumnas().get(j).getTexto()).append("</td>\n");
                    sb.append("<td class=\'NA_dato\'>");
                    if (todo && !t.getColumnas().get(j).isCapturable()) {
                        if (t.getColumnas().get(j).getReferencia() != null) {
                            sb.append(despliegaCombo(mc, t.getColumnas().get(j).getReferencia(), t.getColumnas().get(j).getNombre(), "[--- Seleccione una opci&oacute;n ---]", ""+data.get(t.getColumnas().get(j).getNombre())));
                        } 
                        else {
                            switch (t.getColumnas().get(j).getTipo()) {
                                case ENTERO: {
                                    sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=50 value='").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "" ).append("' />");
                                    break;
                                }
                                case DECIMAL: {
                                    sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=50 value='").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "" ).append("' />");
                                    break;
                                }
                                case BOLEANO: {
                                    sb.append("<input type='radio' name='").append(t.getColumnas().get(j).getNombre()).append("' value='0' ").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null && !((Boolean)data.get(t.getColumnas().get(j).getNombre())).booleanValue()? "checked": "" ).append(" /> No");
                                    sb.append("<input type='radio' name='").append(t.getColumnas().get(j).getNombre()).append("' value='1' ").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null && ((Boolean)data.get(t.getColumnas().get(j).getNombre())).booleanValue()? "checked": "" ).append(" /> Si");
                                    break;
                                }
                                case FECHA: {
                                    sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=10 maxlength=10 value='").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "" ).append("' />");
                                    break;
                                }
                                default: {
                                    sb.append("<input name='").append(t.getColumnas().get(j).getNombre()).append("' size=50 maxlength=").append(t.getColumnas().get(j).getTamano()).append(" value='").append( data!=null && data.get(t.getColumnas().get(j).getNombre())!=null? data.get(t.getColumnas().get(j).getNombre()): "" ).append("' />");
                                }
                            }
                            
                            sb.append("</td>\n");
                            sb.append("</tr>\n");
                        }
                    }
                }
                sb.append("</table>\n");
                sb.append("</td>\n");
                sb.append("</tr>\n");
            }
        }
        
        return sb.toString();
    }

    private static String generaComboCatalogos(MemoryCatalogs mc, String cat) {
        StringBuilder sb = new StringBuilder("");

        sb.append("<tr>\n");
        sb.append("<td>&nbsp;</td>\n");
        sb.append("</tr>\n");
        sb.append("<tr>\n");
        sb.append("<td class=\'NA_catalogos_listaCatalogos\'>\n");
        sb.append("Cat&aacute;logo a administrar\n");
        sb.append("<select name=\'catalogo\'>\n");
        sb.append("<option value=\'\'>[---  Seleccione un catalogo  ---]</option>\n");
        for(MemoryTable t: mc.getCatalogs()){
                sb.append("<option value=\'").append(t.getNombre()).append("\'");
                if (cat != null && cat.equals(t.getNombre()) != false) {
                    sb.append(" selected");
                }
                sb.append(">").append(t.getTexto()).append("</option>\n");
        }
        sb.append("</select>\n");
        sb.append("<input type=\'button\' value=\'Seleccionar\' class=\'NA_boton\' onclick=\'this.form.submit();\' />\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        
        return sb.toString();
    }
}
