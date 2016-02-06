package uk.co.todddavies.literarylog.data.collator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.fileadapter.ReadingFileAdapterModule;
import uk.co.todddavies.literarylog.data.goodreads.GoodreadsApiAdapterModule;

/**
 * Module for the CollatedReadingAdapter.
 * Collates readings from different sources.
 */
public final class CollatedReadingAdapterModule extends AbstractModule {
  
  public CollatedReadingAdapterModule() {}

  @Override
  protected void configure() {
    install(new ReadingFileAdapterModule());
    install(new GoodreadsApiAdapterModule());
    
    bind(ReadingStorageAdapter.class).annotatedWith(CollatedReadings.class).to(CollatedReadingAdapter.class);
  }
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface CollatedReadings {}
}
