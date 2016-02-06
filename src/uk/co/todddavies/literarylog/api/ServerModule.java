package uk.co.todddavies.literarylog.api;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.api.ServerAnnotations.ServiceAddress;
import uk.co.todddavies.literarylog.api.reading.ReadingApiModule;

/**
 * Module for configuring the Rapidoid server
 */
public final class ServerModule extends AbstractModule {
  
  public ServerModule() {}
  
  @Override
  protected void configure() {
    // Install the flag bindings
    install(Flags.flagBindings(ServerFlags.class));
    // Create a multibinder for the different endpoints
    Multibinder<ApiInterface> apis = Multibinder.newSetBinder(binder(), ApiInterface.class);
    // Install api endpoints here
    install(new ReadingApiModule(apis));
    // Bind the url
    bind(String.class).annotatedWith(ServiceAddress.class)
      .toInstance(String.format("http://%s:%s", ServerFlags.getAddress(), ServerFlags.getPort()));
  }

}
