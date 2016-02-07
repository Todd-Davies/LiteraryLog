package uk.co.todddavies.literarylog.data.collator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import com.google.inject.BindingAnnotation;

import edu.uchicago.lowasser.flaginjection.AbstractFlagModule;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.fileadapter.ReadingFileAdapterModule;
import uk.co.todddavies.literarylog.data.goodreads.GoodreadsApiAdapterModule;

/**
 * Module for the CollatedReadingAdapter.
 * Collates readings from different sources.
 */
public final class CollatedReadingAdapterModule extends AbstractFlagModule {
  
  public CollatedReadingAdapterModule() {}

  @Override
  protected void configure() {
    super.configure();
    install(new ReadingFileAdapterModule());
    install(new GoodreadsApiAdapterModule());
    
    bind(ReadingStorageAdapter.class).annotatedWith(CollatedReadings.class).to(CollatedReadingAdapter.class);
  }
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface CollatedReadings {}
}
