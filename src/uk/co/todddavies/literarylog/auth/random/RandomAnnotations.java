package uk.co.todddavies.literarylog.auth.random;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;

/**
 * Defines the annotations commmon to the Random module
 */
public final class RandomAnnotations {

  private RandomAnnotations() {}
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface Seed {}
  
}
