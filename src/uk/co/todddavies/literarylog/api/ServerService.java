package uk.co.todddavies.literarylog.api;


import java.util.Set;

import org.rapidoid.config.Conf;
import org.rapidoid.http.fast.On;
import com.google.inject.Inject;

/**
 * The Rapidoid web server service
 */
public final class ServerService {

  private final Set<ApiInterface> interfaces;
  
  @Inject
  ServerService(Set<ApiInterface> interfaces) {
    Conf.set("address", ServerFlags.getAddress());
    Conf.set("port", ServerFlags.getPort());
    this.interfaces = interfaces;
  }
  
  public void start() {
    On.controllers(interfaces.toArray());
  }
  
}
