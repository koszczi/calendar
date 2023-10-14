package com.koszczi.calendar.web.event;

import com.koszczi.calendar.application.event.EventService;
import com.koszczi.calendar.application.event.dto.EventCreationResult;
import com.koszczi.calendar.application.event.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "events")
public class EventController {

  private final EventService eventService;

  @PostMapping("create")
  public ResponseEntity<EventCreationResult> createEvent(@RequestBody EventDto eventDto) {
    EventCreationResult result = eventService.createEvent(eventDto);
    return switch (result.status()) {
      case SUCCESS -> ResponseEntity.ok(result);
      case VALIDATION_FAILURE -> ResponseEntity.badRequest().body(result);
      case ERROR -> ResponseEntity.internalServerError().build();
    };
  }
}
