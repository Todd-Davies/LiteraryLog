package uk.co.todddavies.literarylog.app;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.api.ServerFlags;
import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;
import uk.co.todddavies.literarylog.auth.random.RandomFlags;
import uk.co.todddavies.literarylog.auth.random.RandomModule;
import uk.co.todddavies.literarylog.auth.twilio.TwilioAuthProviderModule;
import uk.co.todddavies.literarylog.auth.twilio.TwilioFlags;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule;
import uk.co.todddavies.literarylog.data.fileadapter.ReadingFileAdapterFlags;

/**
 * Main entry point for the app.
 */
public final class LiteraryLog {
  
  
  public static void main(String[] args) {    
    // Reconstruct the remaining arguments for the server
    Injector injector = Flags.bootstrapFlagInjector(args,
        new ServerModule(),
        new CollatedReadingAdapterModule(),
        new RandomModule(),
        new InjectionModule(),
        new TwilioAuthProviderModule());
    
    // Populate the static command line argument classes
    injector.getInstance(TwilioFlags.class);
    injector.getInstance(RandomFlags.class);
    injector.getInstance(ReadingFileAdapterFlags.class);
    injector.getInstance(ServerFlags.class);
    
    // Create and start the service
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
  
  private static final class InjectionModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    @Named("main")
    public String mainName() {
      return LiteraryLog.class.getName();
    }
  }
}
