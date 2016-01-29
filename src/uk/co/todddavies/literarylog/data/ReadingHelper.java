package uk.co.todddavies.literarylog.data;

import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.models.Reading;

/**
 * Helper methods for working with <code>Reading</code>s
 */
public final class ReadingHelper {

  private ReadingHelper() {}
  
  /**
   * Is one string contained somewhere inside the other?
   * Non-case sensitive.
   */
  private static final boolean matches(String needle, String haystack) {
    return haystack.toLowerCase().contains(needle.toLowerCase());
  }
  
  /**
   * Determines if some key-value pair matches a reading.
   */
  private static boolean doesApply(Reading reading, String key, String value) {
    switch(key.toLowerCase()) {
    case "name":
      return matches(value, reading.name);
    case "description":
      return matches(value, reading.description);
    case "rating":
      return reading.rating.isPresent() && 
          matches(value, Integer.toString(reading.rating.get()));
    case "link":
      return reading.link.isPresent() &&
          matches(value, reading.link.get());
    case "id":
      return matches(value, Integer.toString(reading.id));
    case "status":
      return matches(value, reading.status.toString()) ||
          matches(value, Integer.toString(reading.status.code));
    case "type":
      return matches(value, reading.type.toString()) ||
          matches(value, Integer.toString(reading.type.code));
    default:
      return true;
    }
  }
  
  /**
   * Does a reading satisfy some search parameters
   */
  public static boolean doesMatch(final Reading reading, final ImmutableMap<String, String> params) {
    for (Entry<String, String> entry : params.entrySet()) {
      if (!doesApply(reading, entry.getKey(), entry.getValue())) return false;
    }
    return true;
  }
  
}
