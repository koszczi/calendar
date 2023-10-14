package com.koszczi.calendar.application.event;

import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.application.event.exception.EventTooLongException;
import com.koszczi.calendar.application.event.exception.InvalidTimeException;
import com.koszczi.calendar.application.event.exception.NotWeekdayException;
import com.koszczi.calendar.application.event.exception.OutOfTimerangeException;
import com.koszczi.calendar.model.event.Event;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventExtractorTests {

  private EventExtractor eventExtractor = new EventExtractor();

  @Test
  public void whenEventDayIsWeekend_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 14, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 14, 11, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(NotWeekdayException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenEventStartsTooEarly_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 8, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(OutOfTimerangeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenEventStartsTooLate_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 18, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 20, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(OutOfTimerangeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenEventEndsTooLate_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 18, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(OutOfTimerangeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenSecondOfStartTimeIsNotZero_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 1);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 17, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(InvalidTimeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenSecondOfEndTimeIsNotZero_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 30, 10);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(InvalidTimeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenMinuteOfStartIsNotZeroOr30_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 15, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(InvalidTimeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenMinuteOfEndtIsNotZeroOr30_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 15, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 16, 2, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(InvalidTimeException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenEventLongerThanAllowed_exception() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 13, 30, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    assertThrows(EventTooLongException.class, () -> eventExtractor.validateDtoAndExtractEvent(eventDto));
  }

  @Test
  public void whenValidationIsSuccessful_eventCreated() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 12, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd);

    Event result = eventExtractor.validateDtoAndExtractEvent(eventDto);
    assertEquals(2023, result.getYear());
    assertEquals(41, result.getWeek());
    assertEquals(DayOfWeek.FRIDAY, result.getDayOfWeek());
    assertEquals(eventStart.toLocalTime(), result.getStart());
    assertEquals(eventEnd.toLocalTime(), result.getEnd());
  }

}
