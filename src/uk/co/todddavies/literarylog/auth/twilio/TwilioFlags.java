package uk.co.todddavies.literarylog.auth.twilio;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;

/**
 * Defines the command line flags for Twilio
 */
public final class TwilioFlags {

  private static String id, token, toNumber, fromNumber;
  private static int timeoutSeconds;
  
  @Inject
  private TwilioFlags(@Flag(name="twilioId", description="The Twilio API ID") String id,
      @Flag(name="twilioToken", description="The Twilio API Token") String token,
      @Flag(name="authNumber", description="The number to send auth SMS messages to") String toNumber,
      @Flag(name="twilioNumber", description="The Twilio number to send SMS messages from") String fromNumber,
      @Flag(name = "twilioRateLimit", description = "The number of seconds between concurrent SMS messages.") int timeoutSeconds) {
    TwilioFlags.id = id;
    TwilioFlags.token = token;
    TwilioFlags.toNumber = toNumber;
    TwilioFlags.fromNumber = fromNumber;
    TwilioFlags.timeoutSeconds = timeoutSeconds;
  }

  static String getId() {
    return id;
  }

  static String getToken() {
    return token;
  }

  static String getToNumber() {
    return toNumber;
  }

  static String getFromNumber() {
    return fromNumber;
  }

  static int getTimeoutSeconds() {
    return timeoutSeconds;
  }
  
}
