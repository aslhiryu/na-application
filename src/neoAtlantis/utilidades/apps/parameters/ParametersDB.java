package neoAtlantis.utilidades.apps.parameters;

import neoAtlantis.utilidades.apps.parameters.objects.MemoryParameters;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import neoAtlantis.utilidades.apps.parameters.interfaces.ParametersLoader;
import neoAtlantis.utilidades.bd.ConfigurationDB;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class ParametersDB implements ParametersLoader {
    static final Logger logger = Logger.getLogger(ParametersDB.class);

    protected StringBuffer sql;
    protected Properties config;

    /**
     * Genera un ParametersLoader por Base de Datos.
     * @param xml Flujo del archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public ParametersDB(InputStream xml) throws Exception {
        this.config=ConfigurationDB.parseConfiguracionXML(xml);
    }

    /**
     * Genera un ParametersLoader por Base de Datos.
     * @param xml Archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public ParametersDB(File xml) throws Exception {
        this(new FileInputStream(xml));
    }

    /**
     * Genera un ParametersLoader por Base de Datos.
     * @param xml Ruta completa del archivo que contiene la configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public ParametersDB(String xml) throws Exception {
        this(new File(xml));
    }

    /**
     * Genera un ParametersLoader por Base de Datos, tomando los valores por default para los campos en la BD.
     * @param configBD Configuraci&oacute;n de acceso a la BD
     * @throws java.lang.Exception
     */
    public ParametersDB(Properties configBD) throws Exception {
        ConfigurationDB.validaConfigProperties(configBD);
        this.config=configBD;
    }

    public ParametersDB(String driver, String url, String user, String pass) throws Exception {
        this.config.setProperty("driver", driver);
        this.config.setProperty("url", url);
        this.config.setProperty("user", user);
        this.config.setProperty("pass", pass);
        ConfigurationDB.validaConfigProperties(this.config);
    }

    //---------------------------------------------------------------------------------

    protected String getQuerySelect(){
        //                  1        2      3       4
        return "SELECT parametro, valor, detalle, tipo FROM parametros";
    }

    protected String getQueryUpdate(){
        //                                  1                 2    
        return "UPDATE parametros SET valor=? WHERE parametro=?";
    }

    //---------------------------------------------------------------------------------

    public void loadParameters(MemoryParameters params) throws Exception {
        this.sql = new StringBuffer(this.getQuerySelect());
        logger.debug("Intenta ejecutar la sentencia '" + this.sql.toString() + "'.");

        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            PreparedStatement ps = con.prepareStatement(sql.toString());
            ResultSet res = ps.executeQuery();
            
            //si existen parametros
            while(res.next()) {
                params.addParam(res.getString(1), res.getString(2), res.getString(3), res.getString(4));
            }
            
            res.close();
            ps.close();
            con.close();
        }catch(Exception ex){
            logger.fatal("No se logro recuperar los parametros.", ex);
            throw ex;
        }
        
    }
    
    public boolean updateParameter(String parameter, String value) throws Exception{
        int i=0;
                
        this.sql = new StringBuffer(this.getQueryUpdate());
        logger.debug("Intenta ejecutar la sentencia '" + this.sql.toString() + "'.");

        try{
            Connection con = ConfigurationDB.generaConexion(this.config);
            PreparedStatement ps = con.prepareStatement(sql.toString());
            ps.setString(1, value);
            ps.setString(2, parameter);
            i=ps.executeUpdate();

            ps.close();
            con.close();
        }catch(Exception ex){
            logger.fatal("No se logro actualizar el parametro '"+parameter+"'.", ex);
            throw ex;
        }
        
        return (i>0? true: false);
    }
    
}
