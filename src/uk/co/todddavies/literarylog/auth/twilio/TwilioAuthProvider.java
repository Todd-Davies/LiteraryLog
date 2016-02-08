package uk.co.todddavies.literarylog.auth.twilio;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.inject.Inject;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;

import uk.co.todddavies.literarylog.api.ServerAnnotations.ServiceAddress;
import uk.co.todddavies.literarylog.auth.AuthProvider;

/**
 * Sends authentication messages over SMS.
 */
final class TwilioAuthProvider implements AuthProvider {

  private static final String AUTH_EXTENSION = "/auth/";
  
  private final TwilioRestClient client;
  private final String url;
  
  private long lastMillis;
  
  @Inject
  TwilioAuthProvider(@ServiceAddress String url) {
    this.url = url;
    this.client = new TwilioRestClient(TwilioFlags.getId(), TwilioFlags.getToken());
    this.lastMillis = 0;
  }

  /**
   * Returns true if a SMS has not been sent within
   * <code>timeoutSeconds</code> seconds.
   */
  private boolean rateLimit() {
    if ((System.currentTimeMillis() - lastMillis) < TwilioFlags.getTimeoutSeconds() * 1000) {
      return true;
    } else {
      lastMillis = System.currentTimeMillis();
      return false;
    }
  }
  
  private boolean authenticate(String message) {
    if (rateLimit()) return false;
    try {
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("Body", message));
      params.add(new BasicNameValuePair("To", TwilioFlags.getToNumber()));
      params.add(new BasicNameValuePair("From", TwilioFlags.getFromNumber()));
   
      client.getAccount().getMessageFactory().create(params);
    } catch (TwilioRestException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  @Override
  public boolean sendAuthCode(String code) {
    return authenticate(url + AUTH_EXTENSION + code);
  }
  
  @Override
  public boolean sendAuthCode(String code, String message) {
    return authenticate(message + "\n" + url + AUTH_EXTENSION + code);
  }

}
