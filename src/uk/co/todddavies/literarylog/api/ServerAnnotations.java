package uk.co.todddavies.literarylog.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;

/**
 * Defines the annotations used in ServerModule
 */
public final class ServerAnnotations {

  private ServerAnnotations() {}
  
  @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
  public @interface ServiceAddress {}
  
}
