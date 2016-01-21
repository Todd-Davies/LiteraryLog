package uk.co.todddavies.literarylog.api.reading;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.models.Reading;

/**
 * Helper class for JSON serialisation.
 */
public final class JsonSerializer {

  private static final String TYPE = "article";
  
  private JsonSerializer() {}
  
  public static final ImmutableMap<String, String> readingToJson(Reading reading) {
    ImmutableMap.Builder<String, String> output = ImmutableMap.builder();
    output.put("name", reading.name);
    output.put("description", reading.description);
    output.put("id", Integer.toString(reading.id));
    output.put("status", reading.status.toString());
    output.put("type", TYPE);
    if (reading.rating.isPresent()) output.put("rating", Integer.toString(reading.rating.get()));
    return output.build();
  }
  
  public static final ImmutableList<ImmutableMap<String, String>> readingsToJson(List<Reading> readings) {
    ImmutableList.Builder<ImmutableMap<String, String>> output = ImmutableList.builder();
    for (Reading reading : readings) {
      output.add(readingToJson(reading));
    }
    return output.build();
  }
  
}
