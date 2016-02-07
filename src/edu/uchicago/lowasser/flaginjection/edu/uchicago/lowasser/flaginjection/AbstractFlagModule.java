package edu.uchicago.lowasser.flaginjection;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;

public abstract class AbstractFlagModule extends AbstractModule {

  @Override
  protected void configure() {
    Reflections reflections = new Reflections(this.getClass().getPackage());
    for (Class<? extends FlagsClass> flagClass : reflections.getSubTypesOf(FlagsClass.class)) {
      install(Flags.flagBindings(flagClass));
    }
  }
  
}
