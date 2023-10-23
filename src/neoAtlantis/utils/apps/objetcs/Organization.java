package neoAtlantis.utils.apps.objetcs;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import neoAtlantis.utilidades.entity.SimpleEntity;

/**
 * Clase que representa a una organizacion
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class Organization extends SimpleEntity implements Serializable{
    private String id;
    private String name;
    private Organization father;
    private String acronym;
    private Boolean active;
    private Date creation;
    private Date modification;
    private Person creator;
    private Person modificator;
    private Map<String,Object> attributes;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the father
     */
    public Organization getFather() {
        return father;
    }

    /**
     * @param father the father to set
     */
    public void setFather(Organization father) {
        this.father = father;
    }

    /**
     * @return the acronym
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * @param acronym the acronym to set
     */
    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the creation
     */
    public Date getCreation() {
        return creation;
    }

    /**
     * @param creation the creation to set
     */
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    /**
     * @return the modification
     */
    public Date getModification() {
        return modification;
    }

    /**
     * @param modification the modification to set
     */
    public void setModification(Date modification) {
        this.modification = modification;
    }

    /**
     * @return the creator
     */
    public Person getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(Person creator) {
        this.creator = creator;
    }

    /**
     * @return the modificator
     */
    public Person getModificator() {
        return modificator;
    }

    /**
     * @param modificator the modificator to set
     */
    public void setModificator(Person modificator) {
        this.modificator = modificator;
    }

    /**
     * @return the attributes
     */
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String,Object> attributes) {
        this.attributes = attributes;
    }

    
}
