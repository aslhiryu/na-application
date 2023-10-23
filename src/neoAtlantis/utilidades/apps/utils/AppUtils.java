package neoAtlantis.utilidades.apps.utils;

import neoAtlantis.utilidades.apps.objects.OperatingSystem;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class AppUtils {
    public static boolean isMovil(OperatingSystem os){
        if(os==OperatingSystem.ANDROID || os==OperatingSystem.PLAYBOOK || 
                os==OperatingSystem.BLACKBERRY || os==OperatingSystem.IOS || 
                os==OperatingSystem.WINDOWS_MOBILE) {
            return true;
        }
        
        return false;
    }
}
