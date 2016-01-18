package uk.co.todddavies.literarylog.models;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ReadingModule extends AbstractModule {

  private static final ImmutableList<Reading> ITEMS = ImmutableList.<Reading>builder()
      .add(new Reading("Test", 0, "This is a good book!"))
      .add(new Reading("Test", 1, "This is a good book!"))
      .add(new Reading("Test", 2, "This is a good book!"))
      .add(new Reading("Test", 3, "This is a good book!"))
      .add(new Reading("Test", 4, "This is a good book!")).build();
  
  @Override
  protected void configure() {
    // TODO: Configure connecting to db etc
  }
  
  @Provides
  ImmutableList<Reading> allReadings() {
    return ITEMS;
  }
}