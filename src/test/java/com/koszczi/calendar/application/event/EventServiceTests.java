package com.koszczi.calendar.application.event;

import com.koszczi.calendar.application.event.dto.EventCreationError;
import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.application.event.dto.ValidationError;
import com.koszczi.calendar.application.event.validation.EventValidator;
import com.koszczi.calendar.model.event.Event;
import com.koszczi.calendar.model.event.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.koszczi.calendar.application.event.dto.EventCreationStatus.*;
import static com.koszczi.calendar.application.event.dto.ValidationError.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

  @Mock
  private EventRepository eventRepository;
  @Mock
  private EventValidator eventValidator;
  @InjectMocks
  private EventService eventService;

  private static final String ORGANIZER = "organizer";

  @Test
  public void whenThereAreValidationFailures_VALIDATION_FAILURE() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 8, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 14, 00, 10);
    EventDto eventDto = new EventDto(eventStart, eventEnd, ORGANIZER);

    Set<ValidationError> validationErrors = prepareDtoValidationErrors();
    when(eventValidator.validateDto(eventDto)).thenReturn(validationErrors);
    when(eventRepository.findAllByDate(eventStart.toLocalDate())).thenReturn(prepareEventsForTheDay());
    when(eventValidator.eventsOverLap(any(Event.class), any(Event.class))).thenReturn(true).thenReturn(false);

    var result = eventService.createEvent(eventDto);
    assertEquals(VALIDATION_FAILURE, result.status());

    assertEquals(4, result.errors().size());
    assertTrue(result.errors().contains(EventCreationError.of(INVALID_TIME)));
    assertTrue(result.errors().contains(EventCreationError.of(EVENT_TOO_LONG)));
    assertTrue(result.errors().contains(EventCreationError.of(OUT_OF_TIMERANGE)));
    assertTrue(result.errors().contains(EventCreationError.of(OVERLAPPING_EVENTS)));

    assertEquals(1, result.overlappingEvents().size());

    assertNull(result.newEvent());
  }

  @Test
  public void whenExceptionIsThrownSomewhere_ERROR() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 8, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 14, 00, 10);
    EventDto eventDto = new EventDto(eventStart, eventEnd, ORGANIZER);

    when(eventValidator.validateDto(eventDto)).thenThrow(new RuntimeException());

    var result = eventService.createEvent(eventDto);
    assertEquals(ERROR, result.status());

    assertEquals(0, result.errors().size());
    assertEquals(0, result.overlappingEvents().size());
    assertNull(result.newEvent());
  }

  @Test
  public void whenValidationPassesAndNoException_SUCCESS() {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 13, 11, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 13, 14, 00, 00);
    EventDto eventDto = new EventDto(eventStart, eventEnd, ORGANIZER);

    when(eventValidator.validateDto(eventDto)).thenReturn(Set.of());
    when(eventRepository.findAllByDate(eventStart.toLocalDate())).thenReturn(List.of());
    when(eventRepository.save(any(Event.class))).thenAnswer(a -> a.getArgument(0) );

    var result = eventService.createEvent(eventDto);
    assertEquals(SUCCESS, result.status());

    assertEquals(0, result.errors().size());
    assertEquals(0, result.overlappingEvents().size());

    assertNotNull(result.newEvent());
  }

  private Set<ValidationError> prepareDtoValidationErrors() {
    Set<ValidationError> validationErrors = new HashSet<>();
    validationErrors.add(EVENT_TOO_LONG);
    validationErrors.add(INVALID_TIME);
    validationErrors.add(OUT_OF_TIMERANGE);
    return validationErrors;
  }

  private List<Event> prepareEventsForTheDay() {
    List<Event> allEventsForTheDay = new ArrayList<>();
    Event overLappingEvent = new Event(
        LocalDateTime.of(2023, 10, 13, 13, 00, 00),
        LocalDateTime.of(2023, 10, 13, 15, 00, 00),
        ORGANIZER
    );
    Event notOverLappingEvent = new Event(
        LocalDateTime.of(2023, 10, 13, 15, 00, 00),
        LocalDateTime.of(2023, 10, 13, 16, 00, 00),
        ORGANIZER
    );

    allEventsForTheDay.add(overLappingEvent);
    allEventsForTheDay.add(notOverLappingEvent);

    return allEventsForTheDay;
  }
}
