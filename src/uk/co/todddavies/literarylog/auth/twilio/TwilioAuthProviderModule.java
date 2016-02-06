package uk.co.todddavies.literarylog.auth.twilio;

import com.google.inject.AbstractModule;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.auth.AuthProvider;

/**
 * Provides a method of authenticating the user using Twilio and SMS
 */
public final class TwilioAuthProviderModule extends AbstractModule {
  
  public TwilioAuthProviderModule() {}
  
  @Override
  protected void configure() {
    install(Flags.flagBindings(TwilioFlags.class));
    bind(AuthProvider.class).to(TwilioAuthProvider.class);
  }
}
