package com.koszczi.calendar.application.event.validation;

import com.koszczi.calendar.application.event.dto.ValidationError;
import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.model.event.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static com.koszczi.calendar.application.event.dto.ValidationError.*;
import static org.junit.jupiter.api.Assertions.*;

public class EventValidatorTests {

  private EventValidator eventValidator = new EventValidator();

  @Test
  public void dtoValidation_whenEventDayIsWeekend_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 14, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 14, 11, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(NOT_WEEKDAY));
  }

  @Test
  public void dtoValidation_whenEventStartsTooEarly_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 8, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(OUT_OF_TIMERANGE));
  }

  @Test
  public void dtoValidation_whenEventStartsTooLate_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 18, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 20, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(OUT_OF_TIMERANGE));
  }

  @Test
  public void dtoValidation_whenEventEndsTooLate_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 18, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(OUT_OF_TIMERANGE));
  }

  @Test
  public void dtoValidation_whenSecondOfStartTimeIsNotZero_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 1);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 17, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(INVALID_TIME));
  }

  @Test
  public void dtoValidation_whenSecondOfEndTimeIsNotZero_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 30, 10);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(INVALID_TIME));
  }

  @Test
  public void dtoValidation_whenMinuteOfStartIsNotZeroOr30_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 15, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(INVALID_TIME));
  }

  @Test
  public void dtoValidation_whenMinuteOfEndtIsNotZeroOr30_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 15, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 2, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(INVALID_TIME));
  }

  @Test
  public void dtoValidation_whenEventLongerThanAllowed_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 13, 30, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(1, failures.size());
    assertTrue(failures.contains(EVENT_TOO_LONG));
  }

  @Test
  public void dtoValidation_whenValidationIsSuccessful_noException() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 12, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(0, failures.size());
  }

  @Test
  public void dtoValidation_whenMultipleProblems_allReturnInSet() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 14, 5, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 14, 18, 00, 10);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Set<ValidationError> failures = eventValidator.validateDto(eventDto);
    assertEquals(4, failures.size());
    assertTrue(failures.contains(EVENT_TOO_LONG));
    assertTrue(failures.contains(INVALID_TIME));
    assertTrue(failures.contains(NOT_WEEKDAY));
    assertTrue(failures.contains(OUT_OF_TIMERANGE));
  }

  @Test
  public void whenRightOverlap_eventsOverlapTrue() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 10, 00, 00),
        LocalDateTime.of(2023, 10, 13, 12, 00, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 13, 00, 00)
    );
    assertTrue(eventValidator.eventsOverLap(event1, event2));
  }

  @Test
  public void whenLeftOverlap_eventsOverlapTrue() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 13, 00, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 10, 00, 00),
        LocalDateTime.of(2023, 10, 13, 12, 00, 00)
    );
    assertTrue(eventValidator.eventsOverLap(event1, event2));
  }

  @Test
  public void whenEvent1EncapsulatesEvent2_eventsOverlapTrue() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 14, 00, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 12, 00, 00),
        LocalDateTime.of(2023, 10, 13, 13, 00, 00)
    );
    assertTrue(eventValidator.eventsOverLap(event1, event2));
  }

  @Test
  public void whenEvent2EncapsulatesEvent1_eventsOverlapTrue() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 11, 30, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 13, 00, 00)
    );
    assertTrue(eventValidator.eventsOverLap(event1, event2));
  }

  @Test
  public void whenEvent1EqualsEvent2_eventsOverlapTrue() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 11, 30, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 11, 30, 00)
    );
    assertTrue(eventValidator.eventsOverLap(event1, event2));
  }


  @Test
  public void whenEventsAreFarFromEachOther_eventsOverlapFalse() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 11, 30, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 13, 00, 00),
        LocalDateTime.of(2023, 10, 13, 13, 30, 00)
    );
    assertFalse(eventValidator.eventsOverLap(event1, event2));
  }

  @Test
  public void whenEvent1EndsWhereEvent2Starts_eventsOverlapFalse() {
    Event event1 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 00, 00),
        LocalDateTime.of(2023, 10, 13, 11, 30, 00)
    );
    Event event2 = new Event(
        LocalDateTime.of(2023, 10, 13, 11, 30, 00),
        LocalDateTime.of(2023, 10, 13, 13, 30, 00)
    );
    assertFalse(eventValidator.eventsOverLap(event1, event2));
  }

}
