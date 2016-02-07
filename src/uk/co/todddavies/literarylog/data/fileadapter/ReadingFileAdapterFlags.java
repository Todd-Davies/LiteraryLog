package uk.co.todddavies.literarylog.data.fileadapter;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;
import edu.uchicago.lowasser.flaginjection.FlagsClass;

/**
 * Defines the command line flags for the FileAdapter
 */
public final class ReadingFileAdapterFlags extends FlagsClass {

  private static String path;
  
  @Inject
  private ReadingFileAdapterFlags(@Flag(name="readingsPath", description="The path to where to store the readings") String path) {
    ReadingFileAdapterFlags.path = path;
  }

  static String getPath() {
    return path;
  }
}
