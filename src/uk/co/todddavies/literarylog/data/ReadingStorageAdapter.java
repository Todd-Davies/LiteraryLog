package uk.co.todddavies.literarylog.data;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

public interface ReadingStorageAdapter {

  /**
   * Gets all the readings from the storage system
   */
  public ImmutableList<Reading> getReadings();
  
  /**
   * Gets all the readings with a specific status
   * @return
   */
  public ImmutableList<Reading> getReadingsWithStatus(final Status status);
  
  /**
   * Gets a specific reading from the storage system
   */
  public Optional<Reading> getReadingById(int id);
  
  /**
   * Searches for readings satisfying some criteria
   */
  public ImmutableList<Reading> search(ImmutableMap<String, String> params);
  
  /**
   * Creates a new <code>Reading</code>
   */
  public boolean createReading(Reading reading);
  
}
