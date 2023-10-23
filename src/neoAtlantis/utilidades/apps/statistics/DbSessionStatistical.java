package neoAtlantis.utilidades.apps.statistics;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletContext;
import neoAtlantis.utilidades.apps.escuchadores.AppListener;
import neoAtlantis.utilidades.bd.ConfigurationDB;
import neoAtlantis.utilidades.statistics.exceptions.StatisticException;
import neoAtlantis.utilidades.statistics.exceptions.StatisticsConfigurationException;
import neoAtlantis.utilidades.statistics.objects.DbStatistical;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class DbSessionStatistical extends  DbStatistical {
    private static final Logger DEBUGGER = Logger.getLogger(DbSessionStatistical.class);
    
     protected ServletContext context;

    public DbSessionStatistical(InputStream xml, ServletContext context) throws StatisticsConfigurationException {
        super(xml, "statistics_user_NA");
        this.context = context;
    }

    public DbSessionStatistical(File xml, ServletContext context) throws StatisticsConfigurationException {
        super(xml, "statistics_user_NA");
        this.context = context;
    }

    public DbSessionStatistical(String xml, ServletContext context) throws StatisticsConfigurationException {
        super(xml, "statistics_user_NA");
        this.context = context;
    }

    public DbSessionStatistical(Properties configBD, ServletContext context) throws StatisticsConfigurationException {
        super(configBD, "statistics_user_NA");
        this.context = context;
    }

    public DbSessionStatistical(String driver, String url, String user, String pass, ServletContext context) throws StatisticsConfigurationException {
        super(driver, url, user, pass, "statistics_user_NA");
        this.context = context;
    }

    // -------------------------------------------------------------------------
    
    protected String getQueryInsert() {
        //                                              1      2
        return "INSERT INTO statistics_session_NA (sessions, period) VALUES (?, ?)";
    }

    // -------------------------------------------------------------------------
    
    @Override
    public void generate() throws StatisticException {
        Connection con;
        PreparedStatement ps;

        this.sql = new StringBuffer(this.getQueryInsert());
        DEBUGGER.debug("Intenta ejecutar la sentencia '"+this.sql.toString()+"'.");
        try {
            con = ConfigurationDB.generaConexion(this.config);
            ps = con.prepareStatement(this.sql.toString());
            ps.setInt(1, ((List)this.context.getAttribute(AppListener.APP_SESIONES)).size());
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            
            ps.executeUpdate();
            ps.close();
            con.close();
        } 
        catch (Exception ex) {
            DEBUGGER.fatal("No se almacena el valor en la estadistica de sesion.", ex);
            throw new StatisticException(ex);
        }
    }
    
}
