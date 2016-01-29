package uk.co.todddavies.literarylog.data.goodreads;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

// TODO: Clean up
final class GoodreadsApiAdapter implements ReadingStorageAdapter {
  
  private static final ImmutableMap<Status, String> URL_ENDPOINTS = ImmutableMap.<Status, String>of(
      Status.READ, "http://www.goodreads.com/review/list_rss/39367252?shelf=read",
      Status.PENDING, "http://www.goodreads.com/review/list_rss/39367252?shelf=to-read",
      Status.READING, "http://www.goodreads.com/review/list_rss/39367252?shelf=currently-reading");
  
  private static final CacheLoader<String, ImmutableList<Reading>> READING_LOADER =
      new CacheLoader<String, ImmutableList<Reading>>() {   
    @Override
    public ImmutableList<Reading> load(String url) throws Exception {
      return GoodreadsApiParserHelper.getReadingBooks(getResponse(url));
    }    
  };
  
  private static final LoadingCache<String, ImmutableList<Reading>> CACHE =
      CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(10)
        .build(READING_LOADER);

  private static String getResponse(String targetUrl) throws IOException {
    try (Scanner s = new Scanner(new URL(targetUrl).openStream())) {
      return s.useDelimiter("\\A").next();
    }
  }

  @Override
  public ImmutableList<Reading> getReadings() {
    return ImmutableList.of();
  }

  @Override
  public ImmutableList<Reading> getReadingsWithStatus(Status status) {
    try {
      return CACHE.get(URL_ENDPOINTS.get(status));
    } catch(Exception e) {
      // Convert to runtime exception
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Reading> getReadingById(int id) {
    return Optional.absent();
  } 
}