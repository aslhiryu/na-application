package neoatlantis.applications.objetcs;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import neoatlantis.entity.SimpleEntity;

/**
 * Clase que representa a una persona
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class Person extends SimpleEntity implements Serializable {
    private String id;
    private String name;
    private String lastName;
    private String secondLastName;
    private String email;
    private String phone;
    private byte[] photo;
    private Organization organization;
    private MiddlePerson boss;
    private Boolean active;
    private Date creation;
    private Date modification;
    private MiddlePerson creator;
    private MiddlePerson modificator;
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
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the lastName
     */
    public String getSecondLastName() {
        return secondLastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setSecondLastName(String lastName) {
        this.secondLastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the photo
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return the boss
     */
    public MiddlePerson getBoss() {
        return boss;
    }

    /**
     * @param boss the boss to set
     */
    public void setBoss(MiddlePerson boss) {
        this.boss = boss;
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

    public boolean getActiveBool() {
        if( this.active!=null ){
            return this.active;
        }
        else{
            return false;
        }
    }

    public void setActiveBool(boolean active) {
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
    public MiddlePerson getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(MiddlePerson creator) {
        this.creator = creator;
    }

    /**
     * @return the modificator
     */
    public MiddlePerson getModificator() {
        return modificator;
    }

    /**
     * @param modificator the modificator to set
     */
    public void setModificator(MiddlePerson modificator) {
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
    
    public void addAttribute(String att, Object val){
        if( this.attributes==null ){
            this.attributes=new HashMap();
        }
        
        this.attributes.put(att, val);
    }
    
    public Object getAttribute(String att){
        if(this.attributes!=null && this.attributes.containsKey(att)){
            return this.attributes.get(att);
        }
        else{
            return null;
        }
    }
}
