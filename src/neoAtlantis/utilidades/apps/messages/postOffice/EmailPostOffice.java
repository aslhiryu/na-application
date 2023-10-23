package neoAtlantis.utilidades.apps.messages.postOffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import neoAtlantis.utilidades.apps.messages.objects.Message;
import neoAtlantis.utilidades.apps.messages.postOffice.exceptions.PostOfficeException;
import neoAtlantis.utilidades.apps.messages.postOffice.interfaces.PostOffice;
import neoAtlantis.utilidades.mail.ConfigurationMail;
import org.apache.log4j.Logger;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class EmailPostOffice implements PostOffice {
    private static final Logger logger = Logger.getLogger(EmailPostOffice.class);
    
    private Properties config;
    private boolean debug;

    public EmailPostOffice(InputStream xml) throws PostOfficeException {
        try {
            this.config = ConfigurationMail.parseConfiguracionXML(xml);
            ConfigurationMail.validaConfigProperties(this.config);
        } catch (Exception ex) {
            throw new PostOfficeException(ex);
        }
    }

    public EmailPostOffice(File xml) throws FileNotFoundException {
        this(new FileInputStream(xml));
    }

    public EmailPostOffice(String xml) throws FileNotFoundException {
        this(new File(xml));
    }

    public EmailPostOffice(Properties config) throws PostOfficeException {
        try {
            ConfigurationMail.validaConfigProperties(config);
        } catch (Exception ex) {
            throw new PostOfficeException(ex);
        }
    }

    public EmailPostOffice(String titulo, String host, String from, String to) throws PostOfficeException {
        this.config = new Properties();
        this.config.setProperty("title", titulo);
        this.config.setProperty("host", host);
        this.config.setProperty("from", from);
        this.config.setProperty("to", to);
        
        try{
            ConfigurationMail.validaConfigProperties(this.config);
        } catch (Exception ex) {
            throw new PostOfficeException(ex);
        }
    }

    @Override
    public boolean addMessage(Message message) throws PostOfficeException {
        MimeMessage mail;
        Session session;

        try {
            if (this.config.getProperty("jndi") != null && this.config.getProperty("jndi").length() > 0) {
                session = Session.getInstance(ConfigurationMail.parseConfig(this.config));
            } 
            else {
                session = (Session)(new InitialContext()).lookup("java:comp/env/"+this.config.getProperty("jndi"));
            }
            session.setDebug(this.debug);
            mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress(message.getRemitente()));
            mail.addRecipient(RecipientType.TO, new InternetAddress(message.getDestinatario()));
            mail.setSubject(message.getTitulo());
            mail.setContent(message.getMensaje(), "text/html");
            Transport.send(mail);
        } catch (Exception ex) {
            throw new PostOfficeException(ex);
        }
        return true;

    }

    @Override
    public List<Message> getReceivedMessages(String user) {
        return new ArrayList();
    }

    @Override
    public List<Message> getReceivedMessages() {
        return new ArrayList();
    }

    @Override
    public Message readMessage(int message) {
        return null;
    }

}
