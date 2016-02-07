package uk.co.todddavies.literarylog.auth.twilio;

import edu.uchicago.lowasser.flaginjection.AbstractFlagModule;
import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.auth.AuthProvider;

/**
 * Provides a method of authenticating the user using Twilio and SMS
 */
public final class TwilioAuthProviderModule extends AbstractFlagModule {
  
  public TwilioAuthProviderModule() {}
  
  @Override
  protected void configure() {
    super.configure();
    install(Flags.flagBindings(TwilioFlags.class));
    bind(AuthProvider.class).to(TwilioAuthProvider.class);
  }
}
