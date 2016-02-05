package uk.co.todddavies.literarylog.app;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Provides helper methods for parsing command line args
 */
final class ArgsParser {

  private ArgsParser() {}
  
  /**
   * Parses the command line arguments into a map
   * Format is: |name|=|value|
   */
  public static final HashMap<String, String> parseArgs(String[] args) {
    HashMap<String, String> output = new HashMap<>();
    for (String arg : args) {
      int equalsIndex = arg.indexOf("=");
      if (equalsIndex < 0) break;
      output.put(arg.substring(0,equalsIndex), arg.substring(equalsIndex + 1));
    }
    return output;
  }
  
  /**
   * Converts a parsed set of arguments to a String array.
   */
  public static final String[] toStringArray(HashMap<String, String> args) {
    String[] output = new String[args.size()];
    int i = 0;
    for (Entry<String, String> entry : args.entrySet()) {
      output[i++] = entry.getKey() + "=" + entry.getValue();
    }
    return output;
  }
  
}
