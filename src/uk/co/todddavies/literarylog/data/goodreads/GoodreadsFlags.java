package uk.co.todddavies.literarylog.data.goodreads;

import javax.annotation.Nullable;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;
import edu.uchicago.lowasser.flaginjection.FlagsClass;

/**
 * Defines the command line flags for Goodreads
 */
public final class GoodreadsFlags extends FlagsClass {

  private static String userId;
  // Default is 3000 ms
  private static int goodreadsTimeout = 3000;
  
  @Inject
  private GoodreadsFlags(@Flag(name="goodreadsId", description="The goodreads id (something like 39367252)") String id,
      @Nullable
      @Flag(name="goodreadsTimeout", optional=true, description="The goodreads timeout in milliseconds e.g. 3000") Integer timeout) {
    GoodreadsFlags.userId = id;
    if (timeout != null) {
      GoodreadsFlags.goodreadsTimeout = timeout;
    }
  }

  static String getUserId() {
    return userId;
  }
  
  static int getGoodreadsTimeout() {
    return goodreadsTimeout;
  }
}
