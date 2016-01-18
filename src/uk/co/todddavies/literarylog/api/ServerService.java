package uk.co.todddavies.literarylog.api;

import java.util.Set;

import org.rapidoid.http.fast.On;

import com.google.inject.Inject;

public final class ServerService {

  private final Set<ApiInterface> interfaces;
  
  @Inject
  ServerService(Set<ApiInterface> interfaces) {
    this.interfaces = interfaces;
  }
  
  public void start() {
    On.controllers(interfaces.toArray());
  }
  
}
