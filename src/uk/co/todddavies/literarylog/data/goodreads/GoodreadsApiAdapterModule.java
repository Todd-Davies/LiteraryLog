package uk.co.todddavies.literarylog.data.goodreads;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;

/**
 * Module for the GoodreadsApiAdapter.
 */
public final class GoodreadsApiAdapterModule extends AbstractModule {

  private final GoodreadsApiAdapter adapter;
  
  public GoodreadsApiAdapterModule() {
    adapter = new GoodreadsApiAdapter();
  }
  
  @Override
  protected void configure() {
    install(Flags.flagBindings(GoodreadsFlags.class));
  }
  
  @Provides
  @GoodreadsReadings
  ReadingStorageAdapter goodreadsAdapterProvider() {
    return adapter;
  }
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface GoodreadsReadings {}
}
