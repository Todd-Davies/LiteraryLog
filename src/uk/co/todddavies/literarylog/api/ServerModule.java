package uk.co.todddavies.literarylog.api;

import org.rapidoid.config.Conf;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import uk.co.todddavies.literarylog.api.reading.ReadingApiModule;

/**
 * Module for configuring the Rapidoid server
 */
public final class ServerModule extends AbstractModule {
  
  public ServerModule(String[] args) {
    // Configures Rapidoid
    Conf.args(args);
  }
  
  @Override
  protected void configure() {
    Multibinder<ApiInterface> apis = Multibinder.newSetBinder(binder(), ApiInterface.class);
    // Install api endpoings here
    install(new ReadingApiModule(apis));
  }

}
