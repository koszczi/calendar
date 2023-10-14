package com.koszczi.calendar.application.event.validation;

import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.model.event.Event;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class EventValidator {

  public static final LocalTime NINE_IN_THE_MORNING = LocalTime.of(9, 0, 0);
  public static final LocalTime FIVE_IN_THE_EVENING = LocalTime.of(17, 0, 0);
  public static final int ALLOWED_EVENT_LENGTH_MINS = 180;

  public boolean eventsOverLap(Event event1, Event event2) {
    return  (event1.getDate().equals(event2.getDate()))
        && event1.getStart().isBefore(event2.getEnd()) && event2.getStart().isBefore(event1.getEnd());
  }


  public void validateDto(EventDto eventDto) throws NotWeekdayException, OutOfTimerangeException,
      InvalidTimeException, EventTooLongException {
    validateDateTime(eventDto.startDateTime());
    validateDateTime(eventDto.endDateTime());
    validateEventLength(eventDto.startDateTime(), eventDto.endDateTime());
  }

  private void validateDateTime(LocalDateTime dateTime) throws NotWeekdayException, OutOfTimerangeException, InvalidTimeException {
    validateWeekDay(dateTime);
    validateTimeRange(dateTime);
    validateAllowedMinuteAndSecond(dateTime);
  }

  private void validateWeekDay(LocalDateTime dateTime) throws NotWeekdayException {
    DayOfWeek dow = dateTime.getDayOfWeek();
    if (SATURDAY.equals(dow) || SUNDAY.equals(dow)) throw new NotWeekdayException();
  }

  private void validateTimeRange(LocalDateTime dateTime) throws OutOfTimerangeException {
    LocalTime time = dateTime.toLocalTime();
    if (time.isBefore(NINE_IN_THE_MORNING) || time.isAfter(FIVE_IN_THE_EVENING))
      throw new OutOfTimerangeException();
  }

  private void validateAllowedMinuteAndSecond(LocalDateTime dateTime) throws InvalidTimeException{
    LocalTime time = dateTime.toLocalTime();
    if (time.getSecond() != 0) throw new InvalidTimeException();
    if (time.getMinute() != 0 && time.getMinute() != 30) throw new InvalidTimeException();
  }

  private void validateEventLength(LocalDateTime startDateTime, LocalDateTime endDateTime) throws EventTooLongException {
    if (startDateTime.until(endDateTime, MINUTES) > ALLOWED_EVENT_LENGTH_MINS) throw new EventTooLongException();
  }
}
