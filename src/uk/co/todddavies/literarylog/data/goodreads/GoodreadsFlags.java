package uk.co.todddavies.literarylog.data.goodreads;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;
import edu.uchicago.lowasser.flaginjection.FlagsClass;

/**
 * Defines the command line flags for Goodreads
 */
public final class GoodreadsFlags extends FlagsClass {

  private static String userId;
  
  @Inject
  private GoodreadsFlags(@Flag(name="goodreadsId", description="The goodreads id (something like 39367252") String id) {
    GoodreadsFlags.userId = id;
  }

  static String getUserId() {
    return userId;
  }
}
