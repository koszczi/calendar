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

import java.util.List;
import java.util.Set;

import static com.koszczi.calendar.application.event.dto.ValidationError.OVERLAPPING_EVENTS;
import static com.koszczi.calendar.application.event.dto.EventCreationStatus.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventService {

  private final EventRepository eventRepository;
  private final EventValidator eventValidator;

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

  private List<String> collectOverlappingEvents(Event newEvent) {
    return eventRepository.findAllByDate(newEvent.getDate())
        .stream()
        .filter(e -> eventValidator.eventsOverLap(newEvent, e))
        .map(Event::toString)
        .toList();

  }
}
