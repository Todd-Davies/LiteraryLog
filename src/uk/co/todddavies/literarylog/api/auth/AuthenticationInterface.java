package uk.co.todddavies.literarylog.api.auth;

/**
 * Interface for issuing an auth challenge to the user.
 */
public interface AuthenticationInterface {
  
  public boolean authChallenge(Runnable function);
  
}
