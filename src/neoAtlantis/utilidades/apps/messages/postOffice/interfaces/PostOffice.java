package neoAtlantis.utilidades.apps.messages.postOffice.interfaces;

import java.util.List;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import neoAtlantis.utilidades.apps.messages.postOffice.exceptions.PostOfficeException;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public interface PostOffice {
    public boolean addMessage(Message message) throws PostOfficeException;
    public List<Message> getReceivedMessages(String user) throws PostOfficeException;
    public List<Message> getReceivedMessages() throws PostOfficeException;
    public Message readMessage(int message) throws PostOfficeException;
}
