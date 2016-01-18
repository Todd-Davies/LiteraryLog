package uk.co.todddavies.literarylog;

import org.rapidoid.annotation.Controller;
import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.todddavies.literarylog.api.ServerModule;
import uk.co.todddavies.literarylog.api.ServerService;

@Controller
public class LiteraryLog {
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ServerModule(args));
    
    ServerService service = injector.getInstance(ServerService.class);
    service.start();
  }
}
