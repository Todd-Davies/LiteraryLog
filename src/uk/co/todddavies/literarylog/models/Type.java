package uk.co.todddavies.literarylog.models;

/**
 * Enum for the type of a reading
 */
public enum Type {
  ARTICLE(1), BOOK(2);
  
  public final int code;
  private Type(int code) {
    this.code = code;
  }
  
  public static Type fromInteger(int code) {
    switch (code) {
    case 1:
      return ARTICLE;
    case 2:
      return BOOK;
    default:
      return null;
    }
  }
}
