package uk.co.todddavies.literarylog.app;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import edu.uchicago.lowasser.flaginjection.Flags;
import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;
import uk.co.todddavies.literarylog.auth.twilio.TwilioAuthProviderModule;
import uk.co.todddavies.literarylog.data.collator.CollatedReadingAdapterModule;

/**
 * Main entry point for the app.
 */
public final class LiteraryLog {
  
  private static final String APP_PACKAGE = "uk.co.todddavies.literarylog";
  
  public static void main(String[] args) {
    // Create the injector
    Injector injector = Flags.bootstrapFlagInjector(args,
        new ServerModule(),
        new CollatedReadingAdapterModule(),
        new TwilioAuthProviderModule(),
        new FlagDebuggerModule());
    
    // Populate the static command line argument classes
    Reflections reflections = new Reflections(APP_PACKAGE);
    for (Class<? extends FlagsClass> flagClass : reflections.getSubTypesOf(FlagsClass.class)) {
      injector.getInstance(flagClass);
    }
    
    // Create and start the service
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
  
  /**
   * Provides a debugging interface for the <code>Flag</code> annotations
   */
  private static final class FlagDebuggerModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    @Named("main")
    public String mainName() {
      return LiteraryLog.class.getName();
    }
  }
}
