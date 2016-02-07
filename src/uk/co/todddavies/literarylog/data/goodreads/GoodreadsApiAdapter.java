package uk.co.todddavies.literarylog.data.goodreads;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.data.ReadingHelper;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

/**
 * Provides an interface to the Goodreads API 
 */
final class GoodreadsApiAdapter implements ReadingStorageAdapter {
  
  private static final ImmutableMap<Status, String> URL_ENDPOINTS = ImmutableMap.<Status, String>of(
      Status.READ, "http://www.goodreads.com/review/list_rss/%s?shelf=read",
      Status.PENDING, "http://www.goodreads.com/review/list_rss/%s?shelf=to-read",
      Status.READING, "http://www.goodreads.com/review/list_rss/%s?shelf=currently-reading");
  
  private static final CacheLoader<String, ImmutableList<Reading>> READING_LOADER =
      new CacheLoader<String, ImmutableList<Reading>>() {   
    @Override
    public ImmutableList<Reading> load(String url) throws Exception {
      return GoodreadsApiParserHelper.getReadingBooks(getContent(url));
    }    
  };
  
  private static final LoadingCache<String, ImmutableList<Reading>> CACHE =
      CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(10)
        .build(READING_LOADER);
  
  private static final Function<String, ImmutableList<Reading>> GET_READINGS = 
      new Function<String, ImmutableList<Reading>>() {
    @Override
    public ImmutableList<Reading> apply(String url) {
      try {
        return CACHE.get(url);
      } catch (ExecutionException e) {
        e.printStackTrace();
        return ImmutableList.of();
      }
    }
  };

  /**
   * Gets the response content from a URL. 
   */
  private static String getContent(String goodreadsUrl) throws IOException {
    String targetUrl = String.format(goodreadsUrl, GoodreadsFlags.getUserId());
    try (Scanner s = new Scanner(new URL(targetUrl).openStream())) {
      return s.useDelimiter("\\A").next();
    }
  }
  
  @Override
  public ImmutableList<Reading> getReadings() {
    return FluentIterable.from(URL_ENDPOINTS.values())
        .transformAndConcat(GET_READINGS)
        .toList();
  }

  @Override
  public ImmutableList<Reading> getReadingsWithStatus(Status status) {
    return GET_READINGS.apply(URL_ENDPOINTS.get(status));
  }

  @Override
  public Optional<Reading> getReadingById(final int id) {
    return FluentIterable.from(URL_ENDPOINTS.values())
        .transformAndConcat(GET_READINGS)
        .filter(new Predicate<Reading>() {
            @Override
            public boolean apply(Reading item) {
              return item.id == id;
            }
          })
        .first();
  }

  @Override
  public ImmutableList<Reading> search(final ImmutableMap<String, String> params) {
    return FluentIterable.from(URL_ENDPOINTS.values())
        .transformAndConcat(GET_READINGS)
        .filter(new Predicate<Reading>() {
            @Override
            public boolean apply(Reading item) {
              return ReadingHelper.doesMatch(item, params);
            }
          })
        .toList();
  }

  @Override
  public boolean createReading(Reading reading) {
    // Not implemented with the read only API.
    return false;
  }
}