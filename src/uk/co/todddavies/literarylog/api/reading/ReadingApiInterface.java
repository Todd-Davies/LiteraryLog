package uk.co.todddavies.literarylog.api.reading;

import java.util.Map;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.api.auth.AuthenticationInterface;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule.CollatedReadings;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;
import uk.co.todddavies.literarylog.models.Type;

/**
 * Defines and implements the reading API. 
 */
@Controller
public final class ReadingApiInterface implements ApiInterface {
  
  private final ReadingStorageAdapter adapter;
  
  private final AuthenticationInterface authInterface;
  
  private final String AUTH_MESSAGE = "Check your phone for a confirmation code.";
  private final String FAIL_MESSAGE = "Something went wrong :(";
  private final String INVALID_MESSAGE = "Invalid parameters";
  private final String ADD_READING_MSG = "Please verify that you want to add the reading '%s'";
  private final String MARK_READ_MSG = "Please verify that you want to mark '%s' as read";
  
  @Inject
  private ReadingApiInterface(@CollatedReadings ReadingStorageAdapter adapter,
      AuthenticationInterface authInterface) {
    this.adapter = adapter;
    this.authInterface = authInterface;
  }
  
  @GET(uri="/")
  public ImmutableList<ImmutableMap<String, String>> home() {
    return JsonSerializer.readingsToJson(adapter.getReadingsWithStatus(Status.READING));
  }

  @GET
  public ImmutableMap<String, String> item(int id) {
    Optional<Reading> result = adapter.getReadingById(id);
    return result.isPresent() 
        ? JsonSerializer.readingToJson(result.get())
        : null;
  }
  
  @GET
  public ImmutableList<ImmutableMap<String, String>> reading() { 
    return JsonSerializer.readingsToJson(adapter.getReadingsWithStatus(Status.READING));
  }
  
  @GET
  public ImmutableList<ImmutableMap<String, String>> read() { 
    return JsonSerializer.readingsToJson(adapter.getReadingsWithStatus(Status.READ));
  }
  
  @GET(uri="/to-read")
  public ImmutableList<ImmutableMap<String, String>> toread() { 
    return JsonSerializer.readingsToJson(adapter.getReadingsWithStatus(Status.PENDING));
  }
  
  @GET(uri="/search")
  public ImmutableList<ImmutableMap<String, String>> search(Map<String, String> params) {
    return JsonSerializer.readingsToJson(
        adapter.search(
            ImmutableMap.<String, String>builder().putAll(params).build()));
  }
  
  @GET(uri="/createReading")
  public String createReading(Map<String, String> params) {
    try {
      Reading.Builder readingBuilder = Reading.newBuilder(
          params.get("name"),
          params.get("description"),
          Status.valueOf(params.get("status").toUpperCase()),
          Type.valueOf(params.get("type").toUpperCase()));
      if (params.containsKey("rating")) {
        readingBuilder.setRating(Integer.parseInt(params.get("rating")));
      }
      if (params.containsKey("link")) {
        readingBuilder.setLink("http://" + params.get("link"));
      }
      final Reading reading = readingBuilder.build();
      Runnable addReading = new Runnable(){
        @Override
        public void run() {
          adapter.createReading(reading);
        }};
      if (authInterface.authChallenge(addReading, String.format(ADD_READING_MSG, reading.name))) {
        return AUTH_MESSAGE;
      } else {
        return FAIL_MESSAGE;
      } 
    } catch (Exception e) {
      return INVALID_MESSAGE;
    }
  }
  
  @GET(uri="/markRead")
  public String markRead(final int id) {
    Runnable markRead = new Runnable(){
      @Override
      public void run() {
        adapter.changeReadingStatus(id, Status.READ);
      }};
    Optional<Reading> readingWithId = adapter.getReadingById(id);
    String msg = readingWithId.isPresent()
        ? readingWithId.get().name
        : "id:" + Integer.toString(id);
    if (authInterface.authChallenge(markRead, String.format(MARK_READ_MSG, msg))) {
      return AUTH_MESSAGE;
    } else {
      return FAIL_MESSAGE;
    }   
  }
}
