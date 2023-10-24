package neoatlantis.applications.parameters.objects;

import neoatlantis.entity.SimpleEntity;

/**
 * Objeto que representa un parametros en memoria
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class Parameter extends SimpleEntity {
    private String parameter;
    private String value;
    private String description;
    private ParameterType type;
   
    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the type
     */
    public ParameterType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ParameterType type) {
        this.type = type;
    }
    
}
