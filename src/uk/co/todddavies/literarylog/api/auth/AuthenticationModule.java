package uk.co.todddavies.literarylog.api.auth;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.auth.random.RandomModule;

/**
 * Module for authentication.
 */
public final class AuthenticationModule extends AbstractModule {

  public AuthenticationModule(Multibinder<ApiInterface> apis) {
    // Configure web service bindings here
    apis.addBinding().to(AuthenticationApiInterface.class);
  }
  
  @Override
  protected void configure() {
    install(new RandomModule());
    bind(AuthenticationInterface.class).to(AuthenticationApiInterface.class);
  }

}
