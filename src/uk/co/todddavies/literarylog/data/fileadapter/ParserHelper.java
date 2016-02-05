package uk.co.todddavies.literarylog.data.fileadapter;

import com.google.common.base.Optional;

import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;
import uk.co.todddavies.literarylog.models.Type;

/**
 * Helper methods for parsing readings
 */
final class ParserHelper {

  // The default type for a reading if it is not set
  // For backwards compatibility when this was not required
  private static final Type DEFAULT_TYPE = Type.ARTICLE;

  /**
   * Parses a Reading from a String
   */
  static Optional<Reading> parseReading(String input) {
    String[] lines = input.split("\n");
    Reading.Builder builder = Reading.newBuilder();
    builder.setType(DEFAULT_TYPE);
    for (String line : lines) {
      int splitIndex = line.indexOf(':');
      if (splitIndex == -1) return Optional.absent();
      String field = line.substring(0, splitIndex).toLowerCase().trim();
      String content = line.substring(splitIndex + 1).trim();
      switch (field) {
      case "name":
        builder.setName(content);
        break;
      case "description":
        builder.setDescription(content);
        break;
      case "rating":
        builder.setRating(Integer.parseInt(content));
        break;
      case "status":
        try {
          builder.setStatus(Status.fromInteger(Integer.parseInt(content)));
        } catch (NumberFormatException e) {
          builder.setStatus(Status.valueOf(content.toUpperCase()));
        }
        break;
      case "type":
        try {
          builder.setType(Type.fromInteger(Integer.parseInt(content)));
        } catch (NumberFormatException e) {
          builder.setType(Type.valueOf(content.toUpperCase()));
        }
        break;
      case "link":
        builder.setLink(content);
        break;
        default:
          return Optional.absent();
      }
    }
    return builder.isComplete() ? Optional.of(builder.build()) : Optional.<Reading>absent();
  }
  
  /**
   * Serialises a Reading into a String.
   */
  static String serializeReading(Reading reading) {
    StringBuilder out = new StringBuilder();
    out.append("name:" + reading.name).append("\n");
    out.append("description:" + reading.description).append("\n");
    out.append("status:" + reading.status.toString()).append("\n");
    out.append("type:" + reading.type.toString()).append("\n");
    if (reading.rating.isPresent()) {
      out.append("rating:" + reading.rating.get().toString()).append("\n");
    }
    if (reading.link.isPresent()) {
      out.append("link:" + reading.link.get()).append("\n");
    }
    return out.toString();
  }
  
}
