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
  
  TwilioAuthProvider(String id,
      String token,
      String toNumber,
      String fromNumber,
      String url) {
    this.client = new TwilioRestClient(id, token);
    this.toNumber = toNumber;
    this.fromNumber = fromNumber;
    this.url = url;
  }

  @Override
  public boolean sendAuthCode(String code) {
    try {
      ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("Body", url + code));
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
