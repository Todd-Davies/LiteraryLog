/**
 * Copyright 2012 Louis Wasserman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uchicago.lowasser.flaginjection;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import com.google.inject.util.Providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.reflections.Reflections;

/**
 * Framework for pulling together the fields annotated with @Flag from a collection of classes, and
 * bootstrapping a Guice module that will provide the flag values at runtime.
 * 
 * <p>
 * The bootstrapping process for flags works like this: you construct modules using
 * {@link #flagBindings} that announce to Guice all of the {@code @Flag}-annotated fields in the
 * specified classes. Calling {@link #bootstrapFlagInjector(String[], Module...)} with these
 * modules and possibly others, Guice first creates an injector capable of generating the
 * {@link Options} object that encapsulates all the flags requested. This base injector, in
 * combination with the specified command line arguments, is used to construct (via injection!) a
 * module that binds all the desired flags to their values. This module is used to create a
 * bootstrapped injector that properly injects flag values to the desired locations.
 * 
 * @author lowasser
 */
public final class Flags {
  private Flags() {
  }

  public static void addFlagBinding(
      Binder binder,
      final Flag flagAnnotation,
      final TypeLiteral<?> parameterType) {
    MapBinder
        .newMapBinder(binder, Flag.class, Type.class)
        .addBinding(flagAnnotation)
        .toInstance(parameterType.getType());
  }

  public static Module flagBindings(final Class<?>... classes) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        for (Class<?> clazz : classes) {
          addFlagBindings(binder(), TypeLiteral.get(clazz));
        }
      }
    };
  }

  public static void addFlagBindings(Binder binder, TypeLiteral<?> literal) {
    for (Field field : literal.getRawType().getDeclaredFields()) {
      if (field.isAnnotationPresent(Flag.class)) {
        Flag annot = field.getAnnotation(Flag.class);
        addFlagBinding(binder, annot, literal.getFieldType(field));
      }
    }
    for (Constructor<?> constructor : literal.getRawType().getDeclaredConstructors()) {
      List<TypeLiteral<?>> parameterTypes = literal.getParameterTypes(constructor);
      Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
      for (int i = 0; i < parameterTypes.size(); i++) {
        Annotation[] annotations = parameterAnnotations[i];
        TypeLiteral<?> typ = parameterTypes.get(i);
        for (Annotation annot : annotations) {
          if (annot instanceof Flag) {
            addFlagBinding(binder, (Flag) annot, typ);
          }
        }
      }
    }
  }

  public static Injector bootstrapFlagInjector(final String[] args,
      String mainClassName,
      List<String> packages,
      Module... baseModules) {
    Logger logger = Logger.getLogger("org.learningu.scheduling.flags.Flags");
    AbstractModule linkingModule = new AbstractModule() {

      @Override
      protected void configure() {
      }

      @Provides
      @RuntimeArguments
      String[] commandLineArguments() {
        return args;
      }

      @Provides
      @Singleton
      Options options(Map<Flag, Type> flagsMap) {
        Options options = new Options();
        for (Flag flag : flagsMap.keySet()) {
          OptionBuilder.hasArgs();
          OptionBuilder.withLongOpt(flag.name());
          OptionBuilder.withDescription(flag.description());
          OptionBuilder.withArgName(flagsMap.get(flag).toString());
          options.addOption(OptionBuilder.create());
        }
        return options;
      }

      @Provides
      @Singleton
      CommandLine commandLine(Options options, @RuntimeArguments String[] args) {
        try {
          return new PosixParser().parse(options, args);
        } catch (ParseException e) {
          throw Throwables.propagate(e);
        }
      }
    };
    logger.fine("Built Options module");
    Injector baseInjector = Guice.createInjector(new FlagErrorModule(mainClassName),
        Modules.combine(Iterables.concat(
          Arrays.asList(baseModules),
          ImmutableList.of(linkingModule))));
    logger.fine("Bootstrapping flag injector with command line arguments");
    Injector createdInjector = baseInjector.createChildInjector(baseInjector.getInstance(FlagBootstrapModule.class));
    // Use reflection to instantiate the variables in FlagClass classes
    for (String packageName : packages) {
      Reflections reflections = new Reflections(packageName);
      Set<Class<? extends FlagsClass>> classes = reflections.getSubTypesOf(FlagsClass.class);
      for (Class<? extends FlagsClass> flagClass : classes) {
        createdInjector.getInstance(flagClass);
      }
    }
    return createdInjector;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static final class FlagBootstrapModule extends AbstractModule {
    private final CommandLine commandLine;
    private final Map<Flag, Type> flagsMap;
    private final Options options;
    private final String main;

    // injected
    @Inject
    FlagBootstrapModule(
        CommandLine commandLine,
        Map<Flag, Type> flagsMap,
        Options options,
        @Named("main") String main) {
      this.commandLine = commandLine;
      this.flagsMap = flagsMap;
      this.options = options;
      this.main = main;
    }

    @Override
    protected void configure() {
      for (Map.Entry<Flag, Type> entry : flagsMap.entrySet()) {
        Flag flagAnnotation = entry.getKey();
        final TypeLiteral literal = TypeLiteral.get(entry.getValue());

        @Nullable
        final String value = commandLine.getOptionValue(flagAnnotation.name());

        try {
          Object result = Converters.converterFor(literal).parse(value);

          if (result == null) {
            if (flagAnnotation.optional()) {
              bind(literal).annotatedWith(flagAnnotation).toProvider(Providers.of(null));
              continue;
            } else {
              throw new RuntimeException();
            }
          } else {
            bind(literal).annotatedWith(flagAnnotation).toInstance(result);
          }
        } catch (RuntimeException e) {
          if (!flagAnnotation.optional()) {
            new HelpFormatter().printHelp(130, main, "", options, "");
            throw Throwables.propagate(new ParseException("Missing flag: " + flagAnnotation));
          }
        }
      }
    }
  }
  
  private static final class FlagErrorModule extends AbstractModule {
    private final String name;
    
    FlagErrorModule(String name) {
      this.name = name;
    }
    
    @Override
    protected void configure() {}

    @Provides
    @Named("main")
    public String mainName() {
      // To print help messages, Apache CLI needs the name of the main class.
      return name;
    }
  }
}
