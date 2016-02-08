package uk.co.todddavies.literarylog.api.auth;

/**
 * Interface for issuing an auth challenge to the user.
 */
public interface AuthenticationInterface {
  
  /**
   * Issue a challenge to the user to authenticate themselves.
   */
  public boolean authChallenge(Runnable function);
  
  /**
   * Issue a challenge to the user to authenticate themselves.
   * Show a message to the user.
   */
  public boolean authChallenge(Runnable function, String message);
  
}
