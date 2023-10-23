package neoAtlantis.utilidades.web;

import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import neoAtlantis.utilidades.apps.catalogs.MessageCatalog;
import neoAtlantis.utilidades.apps.catalogs.MessageType;
import neoAtlantis.utilidades.apps.catalogs.objects.MemoryCatalogs;
import neoAtlantis.utilidades.apps.escuchadores.AppListener;
import neoAtlantis.utilidades.apps.messages.MessagingControl;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import neoAtlantis.utilidades.apps.parameters.ParametersUtils;
import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameters;
import neoAtlantis.utilidades.apps.parameters.objects.ParameterType;
import neoAtlantis.utilidades.debuger.utils.DebugerRequestHtml;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class AppBean {
    private static final Logger LOGGER = Logger.getLogger(AppBean.class);    
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
    
    public static final String USER_REQUEST ="na.request.usuario_mensajes";
    
    //mensajeria
    // -------------------------------------------------------------------------
    
    public static MessagingControl getMessaging(HttpServletRequest request) {
        return (MessagingControl) request.getServletContext().getAttribute(AppListener.APP_MENSAJERIA);
    }
    
     public static String getMemoryMessages(HttpServletRequest request) {
        List<Message> msg;
        Message men;
        String cTmp;
        int menId = 0;
        
        if (request.getParameter("NA_post_office") == null || request.getParameter("NA_post_office").isEmpty() != false) {
            LOGGER.debug("No existen Oficina de Mensajes en memoria.");
            
            return "<font color=\'red\'><b>No se tiene Oficina de Mensajes para trabajar.</b></font>\n";
        }
        if (request.getParameter("NA_user_id") == null || request.getParameter("NA_user_id").isEmpty() != false || (request.getParameter("NA_user_name") == null || request.getParameter("NA_user_name").isEmpty() != false)) {
            LOGGER.debug("No definio el usuario para la lectura de mensajes.");
            
            return "<font color=\'red\'><b>No se defini&oacute; un usuario destinatario.</b></font>\n";
        }
        
        msg = getMessaging(request).getMessages(request.getParameter("NA_post_office"), request.getParameter("NA_user_name"));
        if (msg == null) {
            LOGGER.debug("No existen Oficina de Mensajes en memoria.");
            
            return "<font color=\'red\'><b>No se tiene Oficina de Mensajes para trabajar.</b></font>\n";
        }
        if (request.getParameter("accion") != null && request.getParameter("accion").equals("viewMessage") != false) {
            try {
                menId = Integer.parseInt(request.getParameter("mensaje"));
            } catch (Exception ex) {}
            
            return PostOfficeHtmlFormatter.generaTablaLectura(request, getMessaging(request).getMessage(request.getParameter("NA_post_office"), menId));
        }
        if (request.getParameter("accion") != null && request.getParameter("accion").equals("createMessage") != false) {
            return PostOfficeHtmlFormatter.generaFormaCreacion(request, null);
        }
        if (request.getParameter("accion") != null && request.getParameter("accion").equals("saveMessage") != false) {
            cTmp = PostOfficeHtmlFormatter.validaCampos(request.getParameterMap());
            if (cTmp == null) {
                men = new Message();
                men.setDestinatario(request.getParameter("to"));
                men.setMensaje(request.getParameter("message"));
                men.setRemitente(request.getParameter("NA_user_name"));
                men.setTitulo(request.getParameter("subject"));
                
                return PostOfficeHtmlFormatter.formateaBandejaEntrada(request, msg, new MessageCatalog(MessageType.OK, "Mensaje enviado"));
            }
            
            return PostOfficeHtmlFormatter.generaFormaCreacion(request, new MessageCatalog(cTmp));
        }
        
        return PostOfficeHtmlFormatter.formateaBandejaEntrada(request, msg, null);
    }

    //debug
    // -------------------------------------------------------------------------
    
    public String getDebug(HttpServletRequest request) {
        return DebugerRequestHtml.obtieneDebug(request, true);
    }
    
    //catalogos
    // -------------------------------------------------------------------------
    
     public static MemoryCatalogs getMemoryCatalogs(HttpServletRequest request) {
        return (MemoryCatalogs) request.getServletContext().getAttribute(AppListener.APP_CATALOGOS);

    }

    public List recuperaDatosDeCatalogo(HttpServletRequest request, String catalogo) {
        return getMemoryCatalogs(request).getData(catalogo);
    }

    public String despliegaCombo(HttpServletRequest request, String catalogo, String id) {
        String def=null;

        try {
            def = AppBean.resourceBundle.getString("neoAtlantis.formas.combos.default");
        } catch (Exception ex) {}

        if (def == null || def.isEmpty() != false) {
            def = "[--  Sin Seleccion --]";
        }

        return CatalogsHtmlFormatter.despliegaCombo(getMemoryCatalogs(request), catalogo, id, def);
    }

    public String getAdminCatalogos(HttpServletRequest request) {
        MemoryCatalogs mc = null;
        String cTmp;
        
        mc = (MemoryCatalogs)request.getServletContext().getAttribute(AppListener.APP_CATALOGOS);
        if (mc == null) {
            return "<font color=\'red\'><b>No se tienen Catalogos a administrar.</b></font>\n";
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("selectCatalog") != false && request.getParameter("catalogo") != null && !request.getParameter("catalogo").isEmpty()) {
            return CatalogsHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null);
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("editCatalog") != false) {
            return CatalogsHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null, request.getParameter("registro"), mc.getDataById(mc.getCatalog(request.getParameter("catalogo")), request.getParameter("registro")));
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("addCatalog") != false) {
            return CatalogsHtmlFormatter.generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null, null);
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("addRegistry") != false) {
            cTmp = CatalogsHtmlFormatter.validaCampos(request.getParameterMap(), mc.getCatalog(request.getParameter("catalogo")));
            
            if (cTmp == null) {
                try {
                    mc.addData(mc.getCatalog(request.getParameter("catalogo")), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
                    return CatalogsHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(MessageType.OK, "Registro agregado"));
                } catch (Exception ex) {
                    return CatalogsHtmlFormatter.generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(ex.getMessage()), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
                }                               
            }
            else{
                return CatalogsHtmlFormatter.generaTablaAdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(cTmp), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
            }
        }        
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("modifyRegistry") != false) {
            cTmp = CatalogsHtmlFormatter.validaCampos(request.getParameterMap(), mc.getCatalog(request.getParameter("catalogo")));
            
            if (cTmp == null) {
                try {
                    mc.updateData(mc.getCatalog(request.getParameter("catalogo")), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
                    
                    return CatalogsHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(MessageType.OK, "Registro modificado"));
                } catch (Exception ex) {
                    return CatalogsHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(ex.getMessage()), request.getParameter("registro"), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
                }                                
            }
            else{            
                return CatalogsHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), new MessageCatalog(cTmp), request.getParameter("registro"), CatalogsHtmlFormatter.convierteParametros(request, mc.getCatalog(request.getParameter("catalogo"))));
            }
        }
        else{
            return CatalogsHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mc, request.getParameter("catalogo"), null);
        }
    }
    
    //parametros
    // -------------------------------------------------------------------------
    
     public String despliegaConfiguracion(HttpServletRequest request) {
        MemoryParameters mp=(MemoryParameters) request.getServletContext().getAttribute(AppListener.APP_PARAMETROS);
        String cTmp;
        
        if (mp == null) {
            return "<font color=\'red\'><b>No se tienen Parametros a administrar.</b></font>\n";
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("editParameter") != false && request.getParameter("param") != null && !request.getParameter("param").isEmpty()) {
            return ParametersHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), null);
        }
        else if (request.getParameter("accion") != null && request.getParameter("accion").equals("modifyParam") != false) {
            cTmp = ParametersHtmlFormatter.validaCampo(request.getParameter("value"), request.getParameter("tipo"));
            
            if (cTmp == null) {
                try {
                    mp.updateParam(request.getParameter("param"), request.getParameter("value"));
                    
                    return ParametersHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mp, new MessageCatalog(MessageType.OK, "Parametro modificado"));
                } catch (Exception ex) {
                    return ParametersHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), new MessageCatalog(ex.getMessage()));
                }
            }
            else{
                return ParametersHtmlFormatter.generaTablaEdicion(request.getContextPath()+request.getServletPath(), mp, request.getParameter("param"), new MessageCatalog(cTmp));
            }
        }
        else{
            return ParametersHtmlFormatter.generaTablaAdmin(request.getContextPath()+request.getServletPath(), mp, null);
        }
    }

    public String recuperaParametro(HttpServletRequest request, String parametro) {
        MemoryParameters mp=(MemoryParameters) request.getServletContext().getAttribute(AppListener.APP_PARAMETROS);
        
        return mp.getParameterValue(parametro);
    }

    public int recuperaParametroNumerico(HttpServletRequest request, String parametro) {
        MemoryParameters mp = (MemoryParameters) request.getServletContext().getAttribute(AppListener.APP_PARAMETROS);
        
        if (mp.getParameter(parametro).getTipo() == ParameterType.NUMERICO) {
            try{
                return Integer.parseInt(mp.getParameterValue(parametro));
            }catch(Exception ex){
                return 0;
            }
        }
        
        return -1;
    }

    public boolean recuperaParametroBoleano(HttpServletRequest request, String parametro) {
        MemoryParameters mp = (MemoryParameters) request.getServletContext().getAttribute(AppListener.APP_PARAMETROS);
        
        if (mp.getParameter(parametro).getTipo() == ParameterType.BOLEANO) {
            return ParametersUtils.getBooleanValue(mp.getParameterValue(parametro));
        }
        
        return false;
    }
}
