package uk.co.todddavies.literarylog.auth.random;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;

/**
 * Defines the command line flags for RandomModule
 */
public final class RandomFlags {

  private static int seed;
  
  @Inject
  private RandomFlags(@Flag(name="seed", description="A seed to use for random numbers") int seed) {
    RandomFlags.seed = seed;
  }

  static int getSeed() {
    return seed;
  }
  
}
