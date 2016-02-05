package uk.co.todddavies.literarylog.auth;

/**
 * Some system that sends a message to the user that can
 * be used to verify the user.
 */
public interface AuthProvider {

  /**
   * Sends the auth code to the user
   * @return Whether the operation was successful.
   */
  public boolean sendAuthCode(String string);
  
}
