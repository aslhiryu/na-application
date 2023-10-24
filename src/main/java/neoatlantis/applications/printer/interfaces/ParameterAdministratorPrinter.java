package neoatlantis.applications.printer.interfaces;

import java.util.Map;
import neoatlantis.accesscontroller.printer.exceptions.FormatterException;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface ParameterAdministratorPrinter {
    public Object printAdministration(Map<String,Object> params) throws FormatterException;
}
