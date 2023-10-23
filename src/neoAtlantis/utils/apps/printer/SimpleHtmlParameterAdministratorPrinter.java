package neoAtlantis.utils.apps.printer;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utils.apps.parameters.objects.Parameter;
import neoAtlantis.utils.apps.parameters.objects.MemoryParameters;
import neoAtlantis.utils.apps.printer.exceptions.FormatterException;
import neoAtlantis.utils.apps.printer.interfaces.ParameterAdministratorPrinter;
import neoAtlantis.utils.apps.web.listeners.ApplicationListener;
import neoAtlantis.utils.apps.web.utils.ResourcesLoader;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class SimpleHtmlParameterAdministratorPrinter implements ParameterAdministratorPrinter{
    public static final String REQUEST_KEY="HttpRequest";


    @Override
    public Object printAdministration(Map<String, Object> params) throws FormatterException {
        StringBuilder sb = new StringBuilder("");
        HttpServletRequest request;
        Parameter p;
        String k;
        int i = 0;
        
        if( params.get(REQUEST_KEY)==null ){
            throw new FormatterException("No se proporciono el request para generar el objeto HTML.");
        }
        //obtengo el request
        request=((HttpServletRequest)params.get(REQUEST_KEY));
        //recupero los parametros
        MemoryParameters mp=(MemoryParameters)request.getSession().getServletContext().getAttribute(ApplicationListener.PARAMETERS_KEY);
        Iterator iter=mp.getParametersKeys().iterator();

        sb.append("<script src=\"").append( request.getContextPath() ).append(ResourcesLoader.PATH_UTILS_JS).append("\"></script>\n");
        sb.append("<div class=\"NA_DataList_list\">\n");
        sb.append("<table>\n");
        sb.append("<tr>\n");
        sb.append("<th>Parametro</th>\n");
        sb.append("<th>Valor</th>\n");
        sb.append("<th>Descripci&oacute;n</th>\n");
        sb.append("<th>Tipo</th>\n");
        sb.append("<th>Acciones</th>\n");
        sb.append("</tr>\n");
        while (iter.hasNext()) {
            k = (String) iter.next();
            p = mp.getParameter(k);
            sb.append("<tr>\n");
            sb.append("<td>").append(k).append("</td>\n");
            sb.append("<td><div id=\"NA:ParamValueText:").append(k).append("\">").append(p.getValue()).append("</div><input id=\"NA:ParamValueInput:").append(k).append("\" name=\"NA:ParamValueInput:").append(k).append("\" value=\"").append(p.getValue()).append("\" maxlenght=\"255\" class=\"NA_null\" /></td>\n");
            sb.append("<td>").append(p.getDescription()).append("</td>\n");
            sb.append("<td>").append(p.getType()).append("</td>\n");
            sb.append("<td>").
                    append("<a href=\"#\" id=\"NA:EditParameterButton:").append(k).append("\" class=\"NA_EditParameter_button\" onclick=\"NAAdminParamsEdit('").append(k).append("');\" title=\"Editar Par&aacute;metro\">&nbsp;</a>").
                    append("<a href=\"#\" id=\"NA:SaveParameterButton:").append(k).append("\" class=\"NA_null\" onclick=\"NAAdminParamsSave('").append(k).append("', '").append(request.getSession().getId()).append("');\" title=\"Modifica Par&aacute;metro\">&nbsp;</a>").
                    append("<a href=\"#\" id=\"NA:CancelParameterButton:").append(k).append("\" class=\"NA_null\" onclick=\"NAAdminParamsCancel('").append(k).append("');\" title=\"Cancela Modificaci&oacute;n\">&nbsp;</a>").
                    append("</td>\n");
            sb.append("</tr>\n");
            i++;
        }
        sb.append("</table>\n");
        
        sb.append("<div id=\"NA_Pagination\">\n");
        sb.append("<div class=\"NA_Pagination_registries\">Parametros definidos: ").append(mp!=null? mp.size(): 0).append("</div>\n");
        sb.append("</div>\n");
        sb.append("</div>\n");

        return sb.toString();
    }
    
}
