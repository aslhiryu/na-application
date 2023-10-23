package neoAtlantis.utils.apps.printer.interfaces;

import java.util.Map;
import neoAtlantis.utils.apps.printer.exceptions.FormatterException;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface LogViewerPrinter {
    public static final String NEXT_OPERATION="nextLog";
    public static final String PREVIOUS_OPERATION="prevLog";
    
    public Object printLogViewer(Map<String,Object> params) throws FormatterException;
}
