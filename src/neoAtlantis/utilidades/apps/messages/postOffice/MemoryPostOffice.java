package neoAtlantis.utilidades.apps.messages.postOffice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import neoAtlantis.utilidades.apps.messages.postOffice.exceptions.PostOfficeException;
import neoAtlantis.utilidades.apps.messages.postOffice.interfaces.PostOffice;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class MemoryPostOffice  implements  PostOffice{
    private static final Logger logger = Logger.getLogger(MemoryPostOffice.class);
    
     private  List<Message> mensajes ;

    public MemoryPostOffice(List<Message> mensajes) {
        this.mensajes = mensajes;
        logger.debug("Genero instancia de postOffice: "+this.mensajes);

    }
     
    @Override
    public boolean addMessage(Message message) throws PostOfficeException {
        message.setId(this.generaIdentificador());
        this.mensajes.add(message);
        
        return true;
    }

    @Override
    public List<Message> getReceivedMessages(String user) throws PostOfficeException {
        ArrayList<Message> mTmp = new ArrayList();
        
        for(int i=this.mensajes.size()-1; i>=0; i--){
            if( this.mensajes.get(i).getDestinatario().equalsIgnoreCase(user) ){
                mTmp.add(this.mensajes.get(i));
            }
        }

        return mTmp;
    }

    @Override
    public List<Message> getReceivedMessages() throws PostOfficeException {
        return this.mensajes;
    }

    @Override
    public Message readMessage(int message) throws PostOfficeException {
        for(Message m: this.mensajes){
            if (m.getId() == message) {
                m.setLectura(new Date());
                
                return m;
            }
        }
        
        return null;
    }
    
    // -------------------------------------------------------------------------
    
     private synchronized int generaIdentificador() {
        return (this.mensajes.size() + 1);
    }
}
