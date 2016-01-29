package uk.co.todddavies.literarylog.api.reading;

import java.util.Map;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule.CollatedReadings;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

/**
 * Defines and implements the reading API. 
 */
@Controller
public final class ReadingApiInterface implements ApiInterface {
  
  private final ReadingStorageAdapter adapter;
  
  @Inject
  public ReadingApiInterface(@CollatedReadings ReadingStorageAdapter adapter) {
    this.adapter = adapter;
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
  
  
}
