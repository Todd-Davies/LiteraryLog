package uk.co.todddavies.literarylog.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import uk.co.todddavies.literarylog.app.Args;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;
import uk.co.todddavies.literarylog.auth.twilio.TwilioAuthProviderModule;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule;

/**
 * Main entry point for the app.
 */
public final class LiteraryLog {
  
  private static final ImmutableList<String> REQUIRED_ARGS =
      ImmutableList.of(
          Args.ARG_READINGS_PATH,
          Args.ARG_SEED,
          Args.ARG_TWILIO_ID, Args.ARG_TWILIO_TOKEN,
          Args.ARG_TWILIO_NUMBER, Args.ARG_AUTH_NUMBER,
          Args.ARG_TWILIO_TIMEOUT,
          Args.ARG_ADDRESS, Args.ARG_PORT);
  
  private static void validateArgs(HashMap<String, String> args) {
    for (String arg : REQUIRED_ARGS) {
      if (!args.containsKey(arg)) {
        throw new RuntimeException(String.format("'%s' argument required!", arg));
      }
    }
  }
  
  public static void main(String[] args) {    
    // Parse the arguments into a map
    HashMap<String, String> argMap = ArgsParser.parseArgs(args);
    validateArgs(argMap);
    Path readingsPath = Paths.get(argMap.remove(Args.ARG_READINGS_PATH));
    Integer seed = Integer.parseInt(argMap.remove(Args.ARG_SEED));
    String twilioId = argMap.remove(Args.ARG_TWILIO_ID);
    String twilioToken = argMap.remove(Args.ARG_TWILIO_TOKEN);
    String authNumber = argMap.remove(Args.ARG_AUTH_NUMBER);
    String twilioNumber = argMap.remove(Args.ARG_TWILIO_NUMBER);
    String url = String.format("http://%s:%s", 
        argMap.get(Args.ARG_ADDRESS), argMap.get(Args.ARG_PORT));
    int twilioTimeout = Integer.parseInt(argMap.remove(Args.ARG_TWILIO_TIMEOUT));
    
    // Reconstruct the remaining arguments for the server
    args = ArgsParser.toStringArray(argMap);
    Injector injector = Guice.createInjector(
        new ServerModule(args),
        new CollatedReadingAdapterModule(readingsPath),
        new RandomModule(seed),
        new TwilioAuthProviderModule(
            twilioId,
            twilioToken,
            authNumber,
            twilioNumber,
            url + "/auth",
            twilioTimeout));
    
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
}
