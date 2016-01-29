package uk.co.todddavies.literarylog.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule;

/**
 * Main entry point for the app.
 */
public final class LiteraryLog {
  
  // Example args:
  // port=8080 address=localhost readingsPath="C://Users/Todd/Desktop/readings"
  private static void validateArgs(HashMap<String, String> args) {
    if (!args.containsKey("readingsPath")) {
      throw new RuntimeException("'readingsPath' argument required!");
    }
  }
  
  public static void main(String[] args) {    
    // Parse the arguments into a map
    HashMap<String, String> argMap = ArgsParser.parseArgs(args);
    validateArgs(argMap);
    Path readingsPath = Paths.get(argMap.remove("readingsPath"));
    
    // Reconstruct the remaining arguments for the server
    args = ArgsParser.toStringArray(argMap);
    Injector injector = Guice.createInjector(new ServerModule(args), new CollatedReadingAdapterModule(readingsPath));
    
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
}
