package uk.co.todddavies.literarylog.api.reading;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.api.ApiInterface;
import uk.co.todddavies.literarylog.models.Reading;

@Controller
public class ReadingInterface implements ApiInterface {
  
  private final ImmutableList<Reading> ITEMS;
  
  @Inject
  public ReadingInterface(ImmutableList<Reading> readings) {
    this.ITEMS = readings;
  }
  
  @GET(uri="/")
  public ImmutableList<Reading> home() {
    return ITEMS;
  }

  @GET
  public Reading reading(int id) {
    return ITEMS.get(id);
  }
  
}
