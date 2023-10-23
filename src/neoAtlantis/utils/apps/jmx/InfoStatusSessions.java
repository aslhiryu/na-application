package neoAtlantis.utils.apps.jmx;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface InfoStatusSessions {
    public double getSessionLifetimeMinimal();
    public double getSessionLifetimeMaximum();
    public double getSessionLifetimeAverage();
    public int getActiveSessions();
    public int getActiveSessionsMaximum();
}
