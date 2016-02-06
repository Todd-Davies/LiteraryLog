package uk.co.todddavies.literarylog.auth.random;

import com.google.inject.AbstractModule;
import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.auth.random.RandomAnnotations.Seed;

/**
 * Provides a seed for use with <code>Random</code>
 */
public final class RandomModule extends AbstractModule {
  
  @Override
  protected void configure() {
    install(Flags.flagBindings(RandomFlags.class));
    bind(Integer.class).annotatedWith(Seed.class).toInstance(RandomFlags.getSeed());
  }
}