package uk.co.todddavies.literarylog.app;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;
import uk.co.todddavies.literarylog.auth.twilio.TwilioAuthProviderModule;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule;

/**
 * Main entry point for the app.
 */
public final class LiteraryLog {
  
  public static void main(String[] args) {
    // Create the injector
    Injector injector = Flags.bootstrapFlagInjector(args,
        LiteraryLog.class.getName(),
        ImmutableList.<String>of("uk.co.todddavies.literarylog"),
        new ServerModule(),
        new CollatedReadingAdapterModule(),
        new TwilioAuthProviderModule());
    
    // Create and start the service
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
}
