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
  
  public static void main(String[] args) {    
    // Parse the arguments into a map
    HashMap<String, String> argMap = ArgsParser.parseArgs(args);
    if (!argMap.containsKey("readingsPath")) {
      throw new RuntimeException("'readingsPath' argument required!");
    }
    Path readingsPath = Paths.get(argMap.remove("readingsPath"));
    
    // Reconstruct the remaining arguments for the server
    args = ArgsParser.toStringArray(argMap);
    Injector injector = Guice.createInjector(new ServerModule(args), new CollatedReadingAdapterModule(readingsPath));
    
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
}
