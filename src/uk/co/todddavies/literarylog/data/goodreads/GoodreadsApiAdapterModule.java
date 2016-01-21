package uk.co.todddavies.literarylog.data.goodreads;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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
  protected void configure() {}
  
  @Provides
  ReadingStorageAdapter goodreadsAdapterProvider() {
    return adapter;
  }
}
