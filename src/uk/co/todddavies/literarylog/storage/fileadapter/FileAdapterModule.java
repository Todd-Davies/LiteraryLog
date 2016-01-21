package uk.co.todddavies.literarylog.storage.fileadapter;

import java.nio.file.Path;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import uk.co.todddavies.literarylog.storage.ReadingStorageAdapter;

/**
 * Module for the ReadingFileAdapter.
 * Instances of <code>Reading</code> are read from files in a directory.
 */
public final class FileAdapterModule extends AbstractModule {

  private final ReadingFileAdapter fileAdapter;
  
  public FileAdapterModule(Path storageDirectory) {
    fileAdapter = new ReadingFileAdapter(storageDirectory);
  }
  
  @Override
  protected void configure() {}
  
  @Provides
  ReadingStorageAdapter readingStorageAdapterProvider() {
    return fileAdapter;
  }
}
