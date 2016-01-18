package uk.co.todddavies.literarylog.api.reading;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.models.ReadingModule;

public class ReadingApiModule extends AbstractModule {

  public ReadingApiModule(Multibinder<ApiInterface> apis) {
    // Configure web service bindings here
    apis.addBinding().to(ReadingInterface.class);
  }
  
  @Override
  protected void configure() {
    install(new ReadingModule());
  }

}
