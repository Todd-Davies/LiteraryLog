package uk.co.todddavies.literarylog.data.fileadapter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.data.ReadingHelper;
import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

/**
 * Adapter for loading <code>Reading</code>s from files in a directory.
 * Looks at all files in a directory (plus subdirectories) and treats each one as a separate reading.
 */
final class ReadingFileAdapter implements ReadingStorageAdapter {

  private static final Predicate<Reading> TRUE_PREDICATE = new Predicate<Reading>() {
    @Override public boolean apply(Reading arg0) { return true; }
  };
  
  private static final CacheLoader<Path, Optional<Reading>> READING_LOADER =
      new CacheLoader<Path, Optional<Reading>>() {   
    @Override
    public Optional<Reading> load(Path path) {
      try {
        String content = com.google.common.io.Files.toString(path.toFile(), Charset.defaultCharset());
        return ParserHelper.parseReading(content);
      } catch (Exception e) {
        e.printStackTrace();
        return Optional.absent();
      }
    }    
  };
  
  private final Path storageDirectory;
  private final LoadingCache<Path, Optional<Reading>> cache;
  
  public ReadingFileAdapter(Path storageDirectory) {
    this.storageDirectory = storageDirectory;
    this.cache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(1500)
        .build(READING_LOADER);
  }
  
  /**
   * Tries to load a reading that satisfies the predicate. The first available reading is loaded.
   * TODO: Make this not scan all readings.
   */
  private Optional<Reading> selectReading(Predicate<Reading> predicate) {
    ImmutableList<Reading> satisfied = selectReadings(storageDirectory, cache, predicate);
    return (satisfied.size() > 0)
        ? Optional.<Reading>of(satisfied.get(0))
        : Optional.<Reading>absent(); 
  }
  
  /**
   * Loads all readings that satisfy a predicate.
   */
  private static ImmutableList<Reading> selectReadings(Path path,
      LoadingCache<Path, Optional<Reading>> cache,
      Predicate<Reading> predicate) {
    ImmutableList.Builder<Reading> list = new ImmutableList.Builder<>();
    try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
      for (Path file : stream) {
        if (file.toFile().isDirectory()) {
          list.addAll(selectReadings(file, cache, predicate));
        } else {
          Optional<Reading> reading = cache.get(file);
          if (reading.isPresent() && predicate.apply(reading.get())) {
            list.add(reading.get());
          }
        }
      }
    } catch (IOException | ExecutionException e) {
      // Convert to runtime exception to be caught higher up
      throw new RuntimeException(e);
    }
    return list.build();
  }
  
  @Override
  public ImmutableList<Reading> getReadings() {
    return selectReadings(storageDirectory, cache, TRUE_PREDICATE);
  }

  @Override
  public Optional<Reading> getReadingById(int id) {
    final int finalId = id;
    return selectReading(new Predicate<Reading>() {
      @Override public boolean apply(Reading reading) {
        return reading.id == finalId;
      }});
  }

  @Override
  public ImmutableList<Reading> getReadingsWithStatus(final Status status) {
    return selectReadings(storageDirectory, cache, new Predicate<Reading>() {
      @Override public boolean apply(Reading reading) {
        return reading.status == status;
      }});
  }

  @Override
  public ImmutableList<Reading> search(final ImmutableMap<String, String> params) {
    return selectReadings(storageDirectory, cache, new Predicate<Reading>() {
      @Override public boolean apply(Reading reading) {
        return ReadingHelper.doesMatch(reading, params);
      }});
  } 
}
