package uk.co.todddavies.literarylog.api.reading;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.app.Annotations.Seed;
import uk.co.todddavies.literarylog.auth.AuthProvider;
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
  
  private final Map<String, Reading> authMapping;
  private final Random numberGen;
  private final AuthProvider authProvider;
  
  private final String AUTH_MESSAGE = "Check your phone for a confirmation code.";
  private final String FAIL_MESSAGE = "Something went wrong :(";
  private final String INVALID_MESSAGE = "Invalid parameters";
  private final String AUTH_SUCCESS = "Reading added!";
  private final String AUTH_FAIL = "Invalid auth code.";
  
  @Inject
  private ReadingApiInterface(@CollatedReadings ReadingStorageAdapter adapter,
      @Seed Integer seed,
      AuthProvider authProvider) {
    this.adapter = adapter;
    this.authMapping = new HashMap<>();
    this.numberGen = new Random(seed);
    this.authProvider = authProvider;
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
  
  /**
   * Generates a new code to be used for authentication
   */
  private String generateCode() {
    Integer key = null;
    while (key == null && !authMapping.containsKey(key)) {
      key = numberGen.nextInt(1000000);
    }
    return String.format("%06d", key);
  }
  
  @GET(uri="/createReading")
  public String createReading(Map<String, String> params) {
    String code = generateCode();
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
        readingBuilder.setLink(params.get("link"));
      }
      authMapping.put(code, readingBuilder.build());
      if (authProvider.sendAuthCode(code)) {
        return AUTH_MESSAGE;
      } else {
        return FAIL_MESSAGE;
      } 
    } catch (Exception e) {
      return INVALID_MESSAGE;
    }
  }
  
  @GET
  public String auth(String code) {
    if (authMapping.containsKey(code)) {
      Reading reading = authMapping.remove(code);
      if (adapter.createReading(reading)) {
        return AUTH_SUCCESS;
      } else {
        return AUTH_FAIL;
      }
    } else {
      return FAIL_MESSAGE;
    }
  }
  
}
