package com.koszczi.calendar.web.event;

import com.koszczi.calendar.application.event.EventService;
import com.koszczi.calendar.application.event.dto.EventCreationResult;
import com.koszczi.calendar.application.event.dto.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "events")
public class EventController {

  private final EventService eventService;

  @PostMapping
  public ResponseEntity<EventCreationResult> createEvent(@RequestBody EventDto eventDto) {
    EventCreationResult result = eventService.createEvent(eventDto);
    return switch (result.status()) {
      case SUCCESS -> ResponseEntity.ok(result);
      case VALIDATION_FAILURE -> ResponseEntity.badRequest().body(result);
      case ERROR -> ResponseEntity.internalServerError().build();
    };
  }

  @GetMapping("weeklySchedule")
  public Map<DayOfWeek, List<String>> weeklySchedule(@RequestParam int year, @RequestParam int week) {
    return eventService.generateWeeklySchedule(year, week);
  }

  @GetMapping("dailyFreeSlots")
  public List<String> dailyFreeSlots(@RequestParam LocalDate day) {
    return eventService.collectFreeSlotsForDay(day);
  }

  @GetMapping("isReserved")
  public String isReserved(@RequestParam LocalDateTime dateTime) {
    return eventService.findEventForTime(dateTime);
  }
}
