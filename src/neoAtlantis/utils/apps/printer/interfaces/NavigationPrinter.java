package neoAtlantis.utils.apps.printer.interfaces;

import java.util.Map;
import neoAtlantis.utils.apps.printer.exceptions.FormatterException;

/**
 *
 * @author desarrollo.alberto
 */
public interface NavigationPrinter {    
    public Object printPagination(Map<String,Object> params) throws FormatterException;
    public Object printOrderOption(Map<String,Object> params) throws FormatterException;
}
