package neoAtlantis.utils.apps.web.objects;

import java.io.Serializable;
import java.util.Map;
import neoAtlantis.utilidades.entity.SimpleEntity;

/**
 * Entidad  que representa un peticion de un usuario
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class UserRequest extends SimpleEntity implements Serializable {
    private String url;
    private boolean post;
    private Map<String,String[]> params;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the post
     */
    public boolean isPost() {
        return post;
    }

    /**
     * @param post the post to set
     */
    public void setPost(boolean post) {
        this.post = post;
    }

    /**
     * @return the params
     */
    public Map<String,String[]> getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Map<String,String[]> params) {
        this.params = params;
    }
}
