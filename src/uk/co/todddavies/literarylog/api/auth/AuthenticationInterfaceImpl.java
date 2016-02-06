package uk.co.todddavies.literarylog.api.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.auth.AuthProvider;
import uk.co.todddavies.literarylog.auth.random.RandomAnnotations.Seed;

/**
 * Uses authentication providers to validate that a user is authentic.
 */
@Controller
final class AuthenticationInterfaceImpl implements AuthenticationInterface, ApiInterface {

  private static final String FAIL_MESSAGE = "Something went wrong :(";
  private static final String AUTH_SUCCESS = "Reading added!";
  private static final String AUTH_FAIL = "Invalid auth code.";
  
  private final static Map<String, Runnable> authMapping = new HashMap<>();
  private final Random numberGen;
  private final AuthProvider authProvider;
  
  @Inject
  private AuthenticationInterfaceImpl(@Seed Integer seed, AuthProvider authProvider) { 
    this.numberGen = new Random(seed);
    this.authProvider = authProvider;
  }
  
  /**
   * Generates a new code to be used for authentication
   */
  private String generateCode() {
    Integer key = null;
    while (key == null && !authMapping.containsKey(key)) {
      key = numberGen.nextInt(1000000);
    }
    return String.format("%06d", key);
  }
  
  /**
   * Issue a challenge to the user to authenticate themselves.
   */
  public boolean authChallenge(Runnable function) {
    String code = generateCode();
    authMapping.put(code, function);
    return authProvider.sendAuthCode(code);
  }
  
  @GET(uri="/auth")
  public String auth(String code) {
    if (authMapping.containsKey(code)) {
      try {
        authMapping.remove(code).run();
        return AUTH_SUCCESS;
      } catch (Exception e) {
        e.printStackTrace();
        return FAIL_MESSAGE;
      }
    } else {
      return AUTH_FAIL;
    }
  }
}
