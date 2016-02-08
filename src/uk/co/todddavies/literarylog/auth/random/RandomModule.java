package uk.co.todddavies.literarylog.auth.random;

import com.google.inject.Provides;

import uk.co.todddavies.literarylog.auth.random.RandomAnnotations.Seed;
import edu.uchicago.lowasser.flaginjection.AbstractFlagModule;

/**
 * Provides a seed for use with <code>Random</code>
 */
public final class RandomModule extends AbstractFlagModule {
  
  @Override
  protected void configure() {
    super.configure();
  }
  
  @Provides
  @Seed
  int seedProvider() {
    return RandomFlags.getSeed();
  }
}