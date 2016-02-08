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
  public boolean sendAuthCode(String code);
  
  /**
   * Sends the auth code to the user with the given message
   * @return Whether the operation was successful.
   */
  public boolean sendAuthCode(String code, String message);
  
}
