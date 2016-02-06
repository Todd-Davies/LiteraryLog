package uk.co.todddavies.literarylog.auth.random;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;
import uk.co.todddavies.literarylog.app.FlagsClass;

/**
 * Defines the command line flags for RandomModule
 */
public final class RandomFlags extends FlagsClass {

  private static int seed;
  
  @Inject
  private RandomFlags(@Flag(name="seed", description="A seed to use for random numbers") int seed) {
    RandomFlags.seed = seed;
  }

  static int getSeed() {
    return seed;
  }
  
}
