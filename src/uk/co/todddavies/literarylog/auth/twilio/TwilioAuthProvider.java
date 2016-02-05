package uk.co.todddavies.literarylog.auth.twilio;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import uk.co.todddavies.literarylog.auth.AuthProvider;

/**
 * Sends authentication messages over SMS.
 */
final class TwilioAuthProvider implements AuthProvider {

  private final TwilioRestClient client;
  private final String toNumber, fromNumber, url;
  private final int timeoutSeconds;
  private long lastMillis;
  
  TwilioAuthProvider(String id,
      String token,
      String toNumber,
      String fromNumber,
      String url,
      int timeoutSeconds) {
    this.client = new TwilioRestClient(id, token);
    this.toNumber = toNumber;
    this.fromNumber = fromNumber;
    this.url = url;
    this.timeoutSeconds = timeoutSeconds;
    this.lastMillis = 0;
  }

  /**
   * Returns true if a SMS has not been sent within
   * <code>timeoutSeconds</code> seconds.
   */
  private boolean rateLimit() {
    if ((System.currentTimeMillis() - lastMillis) < timeoutSeconds * 1000) {
      return true;
    } else {
      lastMillis = System.currentTimeMillis();
      return false;
    }
  }
  
  @Override
  public boolean sendAuthCode(String code) {
    if (rateLimit()) return false;
    try {
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("Body", url + '/' + code));
      params.add(new BasicNameValuePair("To", toNumber));
      params.add(new BasicNameValuePair("From", fromNumber));
   
      client.getAccount().getMessageFactory().create(params);
    } catch (TwilioRestException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

}
