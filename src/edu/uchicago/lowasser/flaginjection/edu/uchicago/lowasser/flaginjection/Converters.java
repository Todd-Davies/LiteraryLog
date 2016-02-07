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

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public final class Converters {
  private Converters() {
  }

  private static final Converter<Integer> INT_CONVERTER = new Converter<Integer>() {
    @Override
    public Integer parse(String string) {
      return Integer.valueOf(string);
    }
  };

  private static final Converter<Boolean> BOOLEAN_CONVERTER = new Converter<Boolean>() {
    @Override
    public Boolean parse(String string) {
      if (string == null || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("f")) {
        return false;
      } else {
        return true;
      }
    }
  };

  private static final Converter<String> STRING_CONVERTER = new Converter<String>() {
    @Override
    public String parse(String string) {
      return string;
    }
  };

  private static final Converter<Double> DOUBLE_CONVERTER = new Converter<Double>() {
    @Override
    public Double parse(String string) {
      return Double.valueOf(string);
    }
  };

  private static final Converter<File> FILE_CONVERTER = new Converter<File>() {
    @Override
    public File parse(String string) {
      return new File(string);
    }
  };

  private static final Converter<URI> URI_CONVERTER = new Converter<URI>() {
    @Override
    public URI parse(String string) {
      try {
        return new URI(string);
      } catch (URISyntaxException e) {
        throw Throwables.propagate(e);
      }
    }
  };

  public static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
      .appendDays()
      .appendSuffix("d")
      .printZeroRarelyLast()
      .appendHours()
      .appendSuffix("h")
      .printZeroRarelyLast()
      .appendMinutes()
      .appendSuffix("m")
      .printZeroRarelyLast()
      .appendSecondsWithOptionalMillis()
      .appendSuffix("s")
      .printZeroRarelyLast()
      .toFormatter();

  private static final Converter<Period> PERIOD_CONVERTER = new Converter<Period>() {
    @Override
    public Period parse(String string) {
      return Period.parse(string, PERIOD_FORMATTER);
    }
  };

  private static final Converter<Duration> DURATION_CONVERTER = new Converter<Duration>() {
    @Override
    public Duration parse(String string) {
      return PERIOD_CONVERTER.parse(string).toStandardDuration();
    }
  };

  private static final Converter<Level> LEVEL_CONVERTER = new Converter<Level>() {
    @Override
    public Level parse(String string) {
      return Level.parse(string);
    }
  };

  private static <T extends Enum<T>> Converter<T> enumConverter(final Class<T> clazz) {
    return new Converter<T>() {
      @Override
      public T parse(String string) {
        return Enum.valueOf(clazz, string);
      }
    };
  }

  private static <T> Converter<Optional<T>> optionalConverter(final Converter<T> converter) {
    return new Converter<Optional<T>>() {
      @Override
      public Optional<T> parse(String string) {
        if (string == null || string.isEmpty()) {
          return Optional.absent();
        } else {
          return Optional.of(converter.parse(string));
        }
      }
    };
  }

  private static <T> Converter<List<T>> listConverter(final Converter<T> converter) {
    return new Converter<List<T>>() {
      @Override
      public List<T> parse(String string) {
        Iterable<String> components = Splitter.on(',').trimResults().split(string);
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (String s : components) {
          builder.add(converter.parse(s));
        }
        return builder.build();
      }
    };
  }

  private static <T> Converter<Set<T>> setConverter(final Converter<T> converter) {
    return new Converter<Set<T>>() {
      @Override
      public Set<T> parse(String string) {
        Iterable<String> components = Splitter.on(',').trimResults().split(string);
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (String s : components) {
          builder.add(converter.parse(s));
        }
        return builder.build();
      }
    };
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T> Converter<T> converterFor(TypeLiteral<T> literal) {
    if (literal.getType() instanceof ParameterizedType) {
      Class<?> outer = literal.getRawType();
      TypeLiteral<?> inner = TypeLiteral.get(((ParameterizedType) literal.getType())
          .getActualTypeArguments()[0]);
      Converter<?> innerConverter = converterFor(inner);
      if (outer.equals(List.class)) {
        return (Converter<T>) listConverter(innerConverter);
      } else if (outer.equals(Set.class)) {
        return (Converter<T>) setConverter(innerConverter);
      } else if (outer.equals(Optional.class)) {
        return (Converter<T>) optionalConverter(innerConverter);
      }
    } else if (literal.getRawType().isEnum()) {
      return enumConverter((Class) literal.getRawType());
    } else if (literal.getRawType().equals(Integer.class)
        || literal.getRawType().equals(int.class)) {
      return (Converter<T>) INT_CONVERTER;
    } else if (literal.getRawType().equals(String.class)) {
      return (Converter<T>) STRING_CONVERTER;
    } else if (literal.getRawType().equals(File.class)) {
      return (Converter<T>) FILE_CONVERTER;
    } else if (literal.getRawType().equals(URI.class)) {
      return (Converter<T>) URI_CONVERTER;
    } else if (literal.getRawType().equals(Period.class)) {
      return (Converter<T>) PERIOD_CONVERTER;
    } else if (literal.getRawType().equals(Duration.class)) {
      return (Converter<T>) DURATION_CONVERTER;
    } else if (literal.getRawType().equals(boolean.class)
        || literal.getRawType().equals(Boolean.class)) {
      return (Converter<T>) BOOLEAN_CONVERTER;
    } else if(literal.getRawType().equals(double.class) || literal.getRawType().equals(Double.class)){
      return (Converter<T>) DOUBLE_CONVERTER;
    } else if(literal.getRawType().equals(Level.class)){
      return (Converter<T>) LEVEL_CONVERTER;
    }
    throw new IllegalArgumentException("Don't know what to do with " + literal);
  }
}
