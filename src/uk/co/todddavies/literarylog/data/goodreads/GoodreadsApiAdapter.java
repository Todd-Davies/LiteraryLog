package uk.co.todddavies.literarylog.data.goodreads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;
import uk.co.todddavies.literarylog.models.Type;

// TODO: Clean up
final class GoodreadsApiAdapter implements ReadingStorageAdapter {
  
  private static final String READ_BOOKS_URL = "http://www.goodreads.com/review/list_rss/39367252?shelf=read";
  private static final String PENDING_BOOKS_URL = "http://www.goodreads.com/review/list_rss/39367252?shelf=to-read";
  private static final String READING_BOOKS_URL = "http://www.goodreads.com/review/list_rss/39367252?shelf=currently-reading";
  
  private static final CacheLoader<String, ImmutableList<Reading>> READING_LOADER =
      new CacheLoader<String, ImmutableList<Reading>>() {   
    @Override
    public ImmutableList<Reading> load(String url) throws Exception {
      return getReadingBooks(url);
    }    
  };
  
  private final LoadingCache<String, ImmutableList<Reading>> cache;
  
  public GoodreadsApiAdapter() {
    this.cache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(10)
        .build(READING_LOADER);
  }

  private static String getResponse(String targetUrl) throws IOException {
    try (Scanner s = new Scanner(new URL(targetUrl).openStream())) {
      return s.useDelimiter("\\A").next();
    }
  }
  
  private static Reading parseBook(Element xml) {
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
  
  private static ImmutableList<Reading> getReadingBooks(String url) {
    ImmutableList.Builder<Reading> output = ImmutableList.<Reading>builder(); 
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      String response = getResponse(url);
      StringBuilder xmlStringBuilder = new StringBuilder();
      xmlStringBuilder.append(response);
      ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
      Document doc = builder.parse(input);
      NodeList nodes = doc.getElementsByTagName("item");
      for (int i = 0; i < nodes.getLength(); i++) {
        output.add(parseBook((Element)nodes.item(i)));
      }
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
    return output.build();
  }

  @Override
  public ImmutableList<Reading> getReadings() {
    return ImmutableList.of();
  }

  @Override
  public ImmutableList<Reading> getReadingsWithStatus(Status status) {
    try {
      switch (status) {
      case READING:
        return cache.get(READING_BOOKS_URL);
      case READ:
        return cache.get(READ_BOOKS_URL);
      case PENDING:
        return cache.get(PENDING_BOOKS_URL);
      default:
         return ImmutableList.of();
      }
    } catch(Exception e) {
      // Convert to runtime exception
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Reading> getReadingById(int id) {
    return Optional.absent();
  } 
}