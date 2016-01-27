package uk.co.todddavies.literarylog.data.collator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Path;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.fileadapter.FileAdapterModule;
import uk.co.todddavies.literarylog.data.fileadapter.FileAdapterModule.StorageDirectory;
import uk.co.todddavies.literarylog.data.goodreads.GoodreadsApiAdapterModule;

/**
 * Module for the CollatedReadingAdapter.
 * Collates readings from different sources.
 */
public final class CollatedReadingAdapterModule extends AbstractModule {
  
  private final Path storagePath;
  
  public CollatedReadingAdapterModule(Path storagePath) {
    this.storagePath = storagePath;
  }

  @Override
  protected void configure() {
    install(new FileAdapterModule());
    install(new GoodreadsApiAdapterModule());
    
    bind(ReadingStorageAdapter.class).annotatedWith(CollatedReadings.class).to(CollatedReadingAdapter.class);
  }
  
  @StorageDirectory
  @Provides
  public Path storagePathProvider() {
    return storagePath;
  }
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface CollatedReadings {}
}
