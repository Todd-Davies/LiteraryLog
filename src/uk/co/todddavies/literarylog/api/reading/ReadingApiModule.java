package uk.co.todddavies.literarylog.api.reading;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import uk.co.todddavies.literarylog.api.ApiInterface;

/**
 * Web server module for the Reading API.
 * Binds the API interface to the Rapidoid server.
 */
public final class ReadingApiModule extends AbstractModule {

  public ReadingApiModule(Multibinder<ApiInterface> apis) {
    // Configure web service bindings here
    apis.addBinding().to(ReadingApiInterface.class);
  }
  
  @Override
  protected void configure() {}

}
