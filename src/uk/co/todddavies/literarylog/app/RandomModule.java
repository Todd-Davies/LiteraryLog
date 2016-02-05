package uk.co.todddavies.literarylog.app;

import uk.co.todddavies.literarylog.app.Annotations.Seed;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Provides a seed for use with <code>Random</code>
 */
final class RandomModule extends AbstractModule {
  
  private final int seed;
  
  RandomModule(int seed) {
    this.seed = seed;
  }
  
  @Override
  protected void configure() {}
  
  @Provides
  @Seed
  int provideSeed() {
    return seed;
  }
}