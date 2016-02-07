package uk.co.todddavies.literarylog.api;

import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

import edu.uchicago.lowasser.flaginjection.AbstractFlagModule;
import uk.co.todddavies.literarylog.api.ServerAnnotations.ServiceAddress;
import uk.co.todddavies.literarylog.api.auth.AuthenticationModule;
import uk.co.todddavies.literarylog.api.reading.ReadingApiModule;

/**
 * Module for configuring the Rapidoid server
 */
public final class ServerModule extends AbstractFlagModule {
  
  public ServerModule() {}
  
  @Override
  protected void configure() {
    super.configure();
    // Create a multibinder for the different endpoints
    Multibinder<ApiInterface> apis = Multibinder.newSetBinder(binder(), ApiInterface.class);
    // Install api endpoints here
    install(new ReadingApiModule(apis));
    install(new AuthenticationModule(apis));
  }
  
  @Provides
  @ServiceAddress
  String serviceAddressProvider() {
    return String.format("http://%s:%s", ServerFlags.getAddress(), ServerFlags.getPort());
  }

}
