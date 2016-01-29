package uk.co.todddavies.literarylog.data.goodreads;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;

import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;
import uk.co.todddavies.literarylog.models.Type;

/**
 * Helper class for parsing responses from the Goodreads API
 */
final class GoodreadsApiParserHelper {

  private GoodreadsApiParserHelper() {}
  
  static Reading parseBook(Element xml) {
    Reading.Builder builder = Reading.newBuilder();
    builder.setName(xml.getElementsByTagName("title").item(0).getTextContent());
    builder.setLink(xml.getElementsByTagName("link").item(0).getTextContent());
    builder.setDescription("");
    switch(xml.getElementsByTagName("user_shelves").item(0).getTextContent()) {
    case "currently-reading":
      builder.setStatus(Status.READING);
      break;
    case "to-read":
      builder.setStatus(Status.PENDING);
      break;
    case "read":
    default:
      builder.setStatus(Status.READ);
    }
    builder.setType(Type.BOOK);
    int rating = (int)Float.parseFloat(xml.getElementsByTagName("user_rating").item(0).getTextContent());
    if (rating > 0) builder.setRating(rating);
    return builder.build();
  }
  
  static ImmutableList<Reading> getReadingBooks(String feed) {
    ImmutableList.Builder<Reading> output = ImmutableList.<Reading>builder(); 
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      StringBuilder xmlStringBuilder = new StringBuilder();
      xmlStringBuilder.append(feed);
      ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
      Document doc = builder.parse(input);
      NodeList nodes = doc.getElementsByTagName("item");
      for (int i = 0; i < nodes.getLength(); i++) {
        output.add(GoodreadsApiParserHelper.parseBook((Element)nodes.item(i)));
      }
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
    return output.build();
  }
  
}
