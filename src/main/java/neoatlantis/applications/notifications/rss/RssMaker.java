package neoatlantis.applications.notifications.rss;

import java.io.OutputStream;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Defina la funcionalidad del servicio que entregara el RSS a los clientes
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class RssMaker {
  private OutputStream output;
  private Feed rssfeed;

  public RssMaker(Feed rssfeed, OutputStream output) {
    this.rssfeed = rssfeed;
    this.output = output;
  }

  public void write() throws Exception {
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(this.output);
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    StartDocument startDocument = eventFactory.createStartDocument();

    eventWriter.add(startDocument);

    // create open tag
    eventWriter.add(end);

    StartElement rssStart = eventFactory.createStartElement("", "", "rss");
    eventWriter.add(rssStart);
    eventWriter.add(eventFactory.createAttribute("version", "2.0"));
    eventWriter.add(end);

    eventWriter.add(eventFactory.createStartElement("", "", "channel"));
    eventWriter.add(end);

    createNode(eventWriter, "title", this.rssfeed.getTitle());
    createNode(eventWriter, "link", this.rssfeed.getLink());
    createNode(eventWriter, "description", this.rssfeed.getDescription());
    createNode(eventWriter, "language", this.rssfeed.getLanguage());
    createNode(eventWriter, "copyright", this.rssfeed.getCopyright());
    createNode(eventWriter, "pubdate", this.rssfeed.getPubDate());

    for (FeedMessage entry : this.rssfeed.getMessages()) {
      eventWriter.add(eventFactory.createStartElement("", "", "item"));
      eventWriter.add(end);
      createNode(eventWriter, "title", entry.getTitle());
      createNode(eventWriter, "description", entry.getDescription());
      createNode(eventWriter, "link", entry.getLink());
      createNode(eventWriter, "author", entry.getAuthor());
      createNode(eventWriter, "guid", entry.getGuid());
      eventWriter.add(end);
      eventWriter.add(eventFactory.createEndElement("", "", "item"));
      eventWriter.add(end);

    }

    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement("", "", "channel"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement("", "", "rss"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.close();
  }

  private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");
    XMLEvent tab = eventFactory.createDTD("\t");
    StartElement sElement = eventFactory.createStartElement("", "", name);
    
    eventWriter.add(tab);
    eventWriter.add(sElement);

    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);

    EndElement eElement = eventFactory.createEndElement("", "", name);
    eventWriter.add(eElement);
    eventWriter.add(end);
  }
} 
