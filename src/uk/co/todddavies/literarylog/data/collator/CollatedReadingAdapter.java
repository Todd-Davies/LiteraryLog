package uk.co.todddavies.literarylog.data.collator;

import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import uk.co.todddavies.literarylog.data.ReadingStorageAdapter;
import uk.co.todddavies.literarylog.data.fileadapter.FileAdapterModule.LocalReadings;
import uk.co.todddavies.literarylog.data.goodreads.GoodreadsApiAdapterModule.GoodreadsReadings;
import uk.co.todddavies.literarylog.models.Reading;
import uk.co.todddavies.literarylog.models.Status;

/**
 * Collates the readings from a variety of different adapters
 */
final class CollatedReadingAdapter implements ReadingStorageAdapter {

  private final Set<ReadingStorageAdapter> adapters;
  
  @Inject
  CollatedReadingAdapter(@GoodreadsReadings ReadingStorageAdapter goodreads,
      @LocalReadings ReadingStorageAdapter local) {
    adapters = ImmutableSet.<ReadingStorageAdapter>of(goodreads, local);
  }
  
  @Override
  public ImmutableList<Reading> getReadings() {
    ImmutableList.Builder<Reading> out = ImmutableList.builder();
    System.out.println("Num adapters: " + adapters.size());
    for (ReadingStorageAdapter adapter : adapters) {
      out.addAll(adapter.getReadings());
    }
    return out.build();
  }

  @Override
  public ImmutableList<Reading> getReadingsWithStatus(Status status) {
    ImmutableList.Builder<Reading> out = ImmutableList.builder();
    for (ReadingStorageAdapter adapter : adapters) {
      out.addAll(adapter.getReadingsWithStatus(status));
    }
    return out.build();
  }

  @Override
  public Optional<Reading> getReadingById(int id) {
    for (ReadingStorageAdapter adapter : adapters) {
      Optional<Reading> reading = adapter.getReadingById(id);
      if (reading.isPresent()) return reading;
    }
    return Optional.absent();
  }

  @Override
  public ImmutableList<Reading> search(ImmutableMap<String, String> params) {
    ImmutableList.Builder<Reading> out = ImmutableList.builder();
    for (ReadingStorageAdapter adapter : adapters) {
      out.addAll(adapter.search(params));
    }
    return out.build();
  }

}
