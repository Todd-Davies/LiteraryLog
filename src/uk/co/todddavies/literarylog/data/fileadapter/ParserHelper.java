package uk.co.todddavies.literarylog.data.fileadapter;

import com.google.common.base.Optional;

import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;
import uk.co.todddavies.literarylog.models.Type;

/**
 * Helper methods for parsing readings
 */
final class ParserHelper {

  static Optional<Reading> parseReading(String input) {
    String[] lines = input.split("\n");
    Reading.Builder builder = Reading.newBuilder();
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
        builder.setStatus(Status.fromInteger(Integer.parseInt(content)));
        break;
      case "link":
        builder.setLink(content);
        break;
        default:
          return Optional.absent();
      }
    }
    // All local readings are articles
    builder.setType(Type.ARTICLE);
    return builder.isComplete() ? Optional.of(builder.build()) : Optional.<Reading>absent();
  }
  
}
