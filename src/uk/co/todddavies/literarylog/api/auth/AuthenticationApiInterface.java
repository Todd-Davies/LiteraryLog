package uk.co.todddavies.literarylog.api.auth;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.auth.AuthProvider;
import uk.co.todddavies.literarylog.auth.random.RandomAnnotations.Seed;

/**
 * Uses authentication providers to validate that a user is authentic.
 */
@Controller
final class AuthenticationApiInterface implements AuthenticationInterface, ApiInterface {

  private static final String FAIL_MESSAGE = "Something went wrong :(";
  private static final String AUTH_SUCCESS = "Reading added!";
  private static final String AUTH_FAIL = "Invalid auth code.";
  
  private final static Cache<String, Runnable> cache = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .maximumSize(30)
      .build();
  
  private final Random numberGen;
  private final AuthProvider authProvider;
  
  @Inject
  private AuthenticationApiInterface(@Seed Integer seed, AuthProvider authProvider) { 
    this.numberGen = new Random(seed);
    this.authProvider = authProvider;
  }
  
  /**
   * Generates a new code to be used for authentication
   */
  private String generateCode() {
    Integer key = null;
    while (key == null || cache.getIfPresent(key) != null) {
      key = numberGen.nextInt(1000000);
    }
    return String.format("%06d", key);
  }
  
  /**
   * Issue a challenge to the user to authenticate themselves.
   */
  public boolean authChallenge(Runnable function) {
    String code = generateCode();
    cache.put(code, function);
    return authProvider.sendAuthCode(code);
  }
  
  @GET(uri="/auth")
  public String auth(String code) {
    Runnable runnable = cache.getIfPresent(code);
    if (runnable != null) {
      try {
        runnable.run();
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
