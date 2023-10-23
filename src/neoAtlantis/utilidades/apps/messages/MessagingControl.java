package neoAtlantis.utilidades.apps.messages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import neoAtlantis.utilidades.apps.messages.postOffice.interfaces.PostOffice;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MessagingControl {
     protected Map<String,PostOffice> oficinas=new HashMap<String,PostOffice>();
     
    public void addPostOffice(String id, PostOffice office) {
        if(this.oficinas.get(id)!=null) {
            throw new RuntimeException("La postOffice '"+id+"' ya esta definida.");
        }
        
        this.oficinas.put(id, office);
    }
    
    public void sendMessage(String oficina, Message m) {
        m.setCreacion(new Date());
        
        if(this.oficinas.get(oficina)!=null) {
            this.oficinas.get(oficina).addMessage(m);
        }
    }
    
    public void sendMessage(Message m) {
        Iterator<String> iter=this.oficinas.keySet().iterator();
        String cTmp;

        while( iter.hasNext() ){
            cTmp = iter.next();
            this.sendMessage(cTmp, m);
        }
    }
    
    public List<Message> getMessages(String oficina, String user) {
        ArrayList<Message> lTmp=new ArrayList();

        if (this.oficinas.get(oficina) != null) {
            lTmp.addAll(this.oficinas.get(oficina).getReceivedMessages(user));
        }
        
        return lTmp;
    }
  
    public Message getMessage(String oficina, int id) {
        if( this.oficinas.get(oficina) != null ){
            return this.oficinas.get(oficina).readMessage(id);
        }
                
        return null;

    }
}
