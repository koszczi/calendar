package com.koszczi.calendar.application.event.validation;

import com.koszczi.calendar.application.event.dto.ValidationError;
import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.model.event.Event;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.koszczi.calendar.application.event.dto.ValidationError.*;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class EventValidator {

  public static final LocalTime NINE_IN_THE_MORNING = LocalTime.of(9, 0, 0);
  public static final LocalTime FIVE_IN_THE_EVENING = LocalTime.of(17, 0, 0);
  public static final int MAX_EVENT_LENGTH_MINS = 180;
  public static final int MIN_EVENT_LENGTH_MINS = 30;

  public boolean eventsOverLap(Event event1, Event event2) {
    return event1.getDate().equals(event2.getDate())
        && event1.getStart().isBefore(event2.getEnd())
        && event2.getStart().isBefore(event1.getEnd());
  }

  public Set<ValidationError> validateDto(EventDto eventDto) {
    Set<ValidationError> validationFailures = new HashSet<>();

    validationFailures.addAll(validateDateTime(eventDto.startDateTime()));
    validationFailures.addAll(validateDateTime(eventDto.endDateTime()));
    validateEventNotTooLong(eventDto.startDateTime(), eventDto.endDateTime()).ifPresent(validationFailures::add);
    validateEventNotTooShort(eventDto.startDateTime(), eventDto.endDateTime()).ifPresent(validationFailures::add);

    return validationFailures;
  }

  private Set<ValidationError> validateDateTime(LocalDateTime dateTime) {
    Set<ValidationError> failures = new HashSet<>();

    validateWeekDay(dateTime).ifPresent(failures::add);
    validateTimeRange(dateTime).ifPresent(failures::add);
    validateAllowedMinuteAndSecond(dateTime).ifPresent(failures::add);

    return failures;
  }

  private Optional<ValidationError> validateWeekDay(LocalDateTime dateTime) {
    DayOfWeek dow = dateTime.getDayOfWeek();
    if (SATURDAY.equals(dow) || SUNDAY.equals(dow))
      return Optional.of(NOT_WEEKDAY);
    return Optional.empty();
  }

  private Optional<ValidationError> validateTimeRange(LocalDateTime dateTime) {
    LocalTime time = dateTime.toLocalTime();
    if (time.isBefore(NINE_IN_THE_MORNING) || time.isAfter(FIVE_IN_THE_EVENING))
      return Optional.of(OUT_OF_TIMERANGE);
    return Optional.empty();
  }

  private Optional<ValidationError> validateAllowedMinuteAndSecond(LocalDateTime dateTime) {
    LocalTime time = dateTime.toLocalTime();
    if (time.getSecond() != 0) return Optional.of(INVALID_TIME);
    if (time.getMinute() != 0 && time.getMinute() != 30) return Optional.of(INVALID_TIME);
    return Optional.empty();
  }

  private Optional<ValidationError> validateEventNotTooLong(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return startDateTime.until(endDateTime, MINUTES) > MAX_EVENT_LENGTH_MINS ? Optional.of(EVENT_TOO_LONG) : Optional.empty();
  }

  private Optional<ValidationError> validateEventNotTooShort(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return startDateTime.until(endDateTime, MINUTES) < MIN_EVENT_LENGTH_MINS ? Optional.of(EVENT_TOO_SHORT) : Optional.empty();
  }
}
