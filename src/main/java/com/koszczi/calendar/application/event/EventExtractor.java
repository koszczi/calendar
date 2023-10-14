package com.koszczi.calendar.application.event;

import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.application.event.exception.EventTooLongException;
import com.koszczi.calendar.application.event.exception.InvalidTimeException;
import com.koszczi.calendar.application.event.exception.NotWeekdayException;
import com.koszczi.calendar.application.event.exception.OutOfTimerangeException;
import com.koszczi.calendar.model.event.Event;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.MINUTES;

public class EventExtractor {

  public static final LocalTime NINE_IN_THE_MORNING = LocalTime.of(9, 0, 0);
  public static final LocalTime FIVE_IN_THE_EVENING = LocalTime.of(17, 0, 0);
  public static final int ALLOWED_EVENT_LENGTH_MINS = 180;

  public Event validateDtoAndExtractEvent(EventDto eventDto) throws NotWeekdayException, OutOfTimerangeException,
      InvalidTimeException, EventTooLongException {
    validateDateTime(eventDto.startDateTime());
    validateDateTime(eventDto.endDateTime());
    validateEventLength(eventDto.startDateTime(), eventDto.endDateTime());

    TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    return new Event(
      eventDto.startDateTime().getYear(),
      eventDto.startDateTime().get(woy),
      eventDto.startDateTime().getDayOfWeek(),
      eventDto.startDateTime().toLocalTime(),
      eventDto.endDateTime().toLocalTime()
    );
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
