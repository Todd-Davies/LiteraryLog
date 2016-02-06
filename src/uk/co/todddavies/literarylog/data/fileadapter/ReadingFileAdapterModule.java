package uk.co.todddavies.literarylog.data.fileadapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Paths;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provides;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;

/**
 * Module for the ReadingFileAdapter.
 * Instances of <code>Reading</code> are read from files in a directory.
 */
public final class ReadingFileAdapterModule extends AbstractModule {

  private ReadingFileAdapter fileAdapter;
  
  @Override
  protected void configure() {
    install(Flags.flagBindings(ReadingFileAdapterFlags.class));
  }
  
  @Inject
  @Provides
  @LocalReadings
  ReadingStorageAdapter readingStorageAdapterProvider() {
    if (fileAdapter == null) {
      fileAdapter = new ReadingFileAdapter(Paths.get(ReadingFileAdapterFlags.getPath()));
    }
    return fileAdapter;
  }
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface LocalReadings {}
}
