package uk.co.todddavies.literarylog.data.fileadapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Path;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provides;

import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;

/**
 * Module for the ReadingFileAdapter.
 * Instances of <code>Reading</code> are read from files in a directory.
 */
public final class FileAdapterModule extends AbstractModule {

  private ReadingFileAdapter fileAdapter;
  
  @Override
  protected void configure() {}
  
  @Inject
  @Provides
  @LocalReadings
  ReadingStorageAdapter readingStorageAdapterProvider(@StorageDirectory Path storageDirectory) {
    if (fileAdapter == null) {
      fileAdapter = new ReadingFileAdapter(storageDirectory);
    }
    return fileAdapter;
  }
  
  @BindingAnnotation
  public @interface StorageDirectory {}
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface LocalReadings {}
}
