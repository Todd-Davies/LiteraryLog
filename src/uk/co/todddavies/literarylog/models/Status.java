package uk.co.todddavies.literarylog.models;

/**
 * Enum for the status of a reading (read/not read etc).
 */
public enum Status {
  READ(1), READING(2), PENDING(3);
  
  public final int code;
  private Status(int code) {
    this.code = code;
  }
  
  public static Status fromInteger(int code) {
    switch (code) {
    case 1:
      return READ;
    case 2:
      return READING;
    case 3:
      return PENDING;
    default:
      return null;
    }
  }
}
