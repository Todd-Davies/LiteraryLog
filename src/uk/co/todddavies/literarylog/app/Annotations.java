package uk.co.todddavies.literarylog.app;

import com.google.inject.BindingAnnotation;

/**
 * Defines the annotations commmon to the whole app
 */
public final class Annotations {

  private Annotations() {}
  
  @BindingAnnotation
  public @interface Seed {}
  
}
