package neoAtlantis.utils.apps.parameters.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import neoAtlantis.utilidades.entity.SimpleDAO;
import neoAtlantis.utils.apps.parameters.objects.Parameter;
import neoAtlantis.utils.apps.parameters.objects.ParameterType;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class BasicParametersDAO extends SimpleDAO<Parameter> {   
    private final static Logger DEBUGER=Logger.getLogger(BasicParametersDAO.class);
    
    private String tableName="parametros";    
    
    public BasicParametersDAO(String jndi) {
        super(jndi);
    }
    
    public BasicParametersDAO(Properties config) {
        super(config);
    }
    
    public void setTableName(String name){
        this.tableName=name;
    }
    
    // --------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public Parameter parseEntity(ResultSet res) {
        Parameter p=new Parameter();
        
        try{
            p.setParameter(res.getString("parametro"));
            p.setValue(res.getString("valor"));
            p.setDescription(res.getString("detalle"));
            p.setType(ParameterType.parse(res.getString("tipo")));
        }
        catch(Exception ex){
            DEBUGER.error("No se logro generar el parametro", ex);
        }
        
        return p;
    }
    
    @Override
    public String getSelectByIdQuery() {
        return "SELECT parametro, valor, detalle, tipo FROM "+this.tableName+" WHERE parametro=?";
    }

    @Override
    public String getSelectQuery(Parameter filtro) {
        StringBuilder sb=new StringBuilder("SELECT parametro, valor, detalle, tipo FROM "+this.tableName+" WHERE parametro<>'' ");
        
        if(filtro.getParameter()!=null && !filtro.getParameter().isEmpty()){
            sb.append("AND parametro LIKE ? ");
        }
        if(filtro.getValue()!=null && !filtro.getValue().isEmpty()){
            sb.append("AND valor LIKE ? ");
        }
        if(filtro.getDescription()!=null && !filtro.getDescription().isEmpty()){
            sb.append("AND detalle LIKE ? ");
        }
        if(filtro.getType()!=null){
            sb.append("AND tipo=? ");
        }
        
        return sb.toString();
    }
    
    @Override
    public void asignSelectParameters(PreparedStatement query, Parameter filtro) throws SQLException {
        int pos=1;
        
        if(filtro.getParameter()!=null && !filtro.getParameter().isEmpty()){
            query.setString(pos, "%"+filtro.getParameter()+"%");
            pos++;
        }
        if(filtro.getValue()!=null && !filtro.getValue().isEmpty()){
            query.setString(pos, "%"+filtro.getValue()+"%");
            pos++;
        }
        if(filtro.getDescription()!=null && !filtro.getDescription().isEmpty()){
            query.setString(pos, "%"+filtro.getDescription()+"%");
            pos++;
        }
        if(filtro.getType()!=null){
            query.setString(pos, filtro.getType().getValue());
        }
    }
    
    @Override
    public String getUpdateQuery(Parameter objeto) {
        return "UPDATE "+this.tableName+" SET valor=? WHERE parametro=? ";
    }
    
    @Override
    public void asignUpdateParameters(PreparedStatement query, Parameter objeto) throws SQLException {
        query.setString(1, objeto.getValue());
        query.setString(2, objeto.getParameter());
    }
    
}
