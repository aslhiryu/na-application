package neoAtlantis.utils.apps.printer.interfaces;

import java.util.Map;
import neoAtlantis.utils.apps.printer.exceptions.FormatterException;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface ParameterAdministratorPrinter {
    public Object printAdministration(Map<String,Object> params) throws FormatterException;
}
