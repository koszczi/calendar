package com.koszczi.calendar.application.event;

import com.koszczi.calendar.application.event.dto.EventCreationError;
import com.koszczi.calendar.application.event.dto.ValidationError;
import com.koszczi.calendar.application.event.dto.EventCreationResult;
import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.application.event.validation.EventValidator;
import com.koszczi.calendar.model.event.Event;
import com.koszczi.calendar.model.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.koszczi.calendar.application.event.dto.ValidationError.OVERLAPPING_EVENTS;
import static com.koszczi.calendar.application.event.dto.EventCreationStatus.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventService {

  private final EventRepository eventRepository;
  private final EventValidator eventValidator;
  private final ScheduleGenerator scheduleGenerator;

  public EventCreationResult createEvent(EventDto eventDto) {
    try {
      Set<ValidationError> validationErrors = eventValidator.validateDto(eventDto);
      Event newEvent = new Event(eventDto.startDateTime(), eventDto.endDateTime(), eventDto.organizer());
      List<String> overLappingEvents = collectOverlappingEvents(newEvent);
      if (!overLappingEvents.isEmpty()) validationErrors.add(OVERLAPPING_EVENTS);

      if (validationErrors.isEmpty()) {
        newEvent = eventRepository.save(newEvent);
        return new EventCreationResult(SUCCESS, List.of(), List.of(), newEvent);
      } else {
        return new EventCreationResult(VALIDATION_FAILURE, validationErrors.stream().map(EventCreationError::of).toList(), overLappingEvents, null);
      }

    } catch (Exception e) {
      log.error("Error creating calendar event", e);
      return new EventCreationResult(ERROR, List.of(), List.of(), null);
    }
  }

  public Map<DayOfWeek, List<String>> generateWeeklySchedule(int year, int week) {
    Map<DayOfWeek, List<String>> weeklySchedule = new TreeMap<>();
    EnumSet<DayOfWeek> daysOfWeek = EnumSet.allOf(DayOfWeek.class);
    Collection<Event> weeklyEvents = eventRepository.findAllByYearAndWeek(year, week);
    for (DayOfWeek day: daysOfWeek) {
      if (DayOfWeek.SATURDAY.equals(day))
        break;
      LocalDate actualDate = generateDateFromYearWeekAndDay(year, week, day);
      Collection<Event> dailyEvents = weeklyEvents.stream().filter(e -> day.equals(e.getDayOfWeek())).toList();
      weeklySchedule.put(day, scheduleGenerator.generateDailySchedule(actualDate, dailyEvents));
    }
    return weeklySchedule;
  }

  private LocalDate generateDateFromYearWeekAndDay(int year, int week, DayOfWeek dayOfWeek) {
    return LocalDate.of(year, 1, 1)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
        .with(TemporalAdjusters.previousOrSame(dayOfWeek));
  }

  private List<String> collectOverlappingEvents(Event newEvent) {
    return eventRepository.findAllByDate(newEvent.getDate())
        .stream()
        .filter(e -> eventValidator.eventsOverLap(newEvent, e))
        .map(Event::toString)
        .toList();

  }
}
