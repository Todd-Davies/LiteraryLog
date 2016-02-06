package uk.co.todddavies.literarylog.api;

import com.google.inject.Inject;

import edu.uchicago.lowasser.flaginjection.Flag;

/**
 * Defines the command line flags for the Server
 */
public final class ServerFlags {

  private static String address;
  private static int port;
  
  @Inject
  private ServerFlags(@Flag(name="address", description="The address of the service") String address,
      @Flag(name = "port", description = "The port of the service") int port) {
    ServerFlags.address = address;
    ServerFlags.port= port;
  }

  static String getAddress() {
    return address;
  }

  static int getPort() {
    return port;
  }
  
}
