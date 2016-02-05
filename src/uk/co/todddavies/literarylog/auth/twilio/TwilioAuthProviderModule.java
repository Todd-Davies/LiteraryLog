package uk.co.todddavies.literarylog.auth.twilio;

import uk.co.todddavies.literarylog.auth.AuthProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Provides a method of authenticating the user using Twilio and SMS
 */
public final class TwilioAuthProviderModule extends AbstractModule {
  
  private final TwilioAuthProvider provider;
  
  public TwilioAuthProviderModule(String id,
      String token,
      String toNumber,
      String fromNumber,
      String url) {
    provider = new TwilioAuthProvider(id, token, toNumber, fromNumber, url);
  }
  
  @Override
  protected void configure() {}

  @Provides
  AuthProvider twilioAuthProvider() {
    return provider;
  }
  
}
