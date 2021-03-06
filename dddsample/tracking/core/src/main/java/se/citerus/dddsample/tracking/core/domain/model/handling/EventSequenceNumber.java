package se.citerus.dddsample.tracking.core.domain.model.handling;

import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.concurrent.atomic.AtomicLong;

public class EventSequenceNumber extends ValueObjectSupport<EventSequenceNumber> {

  private final long value;
  private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis());

  private EventSequenceNumber(final long value) {
    this.value = value;
  }

  public static EventSequenceNumber next() {
    return new EventSequenceNumber(SEQUENCE.getAndIncrement());
  }

  public static EventSequenceNumber valueOf(final long value) {
    return new EventSequenceNumber(value);
  }
  
  public long longValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  EventSequenceNumber() {
    // Needed by Hibernate
    value = -1L;
  }

}
