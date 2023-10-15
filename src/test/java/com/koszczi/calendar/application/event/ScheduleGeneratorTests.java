package com.koszczi.calendar.application.event;

import com.koszczi.calendar.model.event.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleGeneratorTests {

  private ScheduleGenerator scheduleGenerator = new ScheduleGenerator();

  @Test
  public void whenNoEvent_oneFreeSlot() {
    LocalDate day = LocalDate.of(2023, 10, 13);
    List<String> result = scheduleGenerator.generateDailySchedule(day, List.of());
    assertEquals(1, result.stream().filter(s -> s.contains(ScheduleGenerator.FREE_SLOT)).count());
    assertEquals(0, result.stream().filter(s -> s.contains(ScheduleGenerator.RESERVED_SLOT)).count());
  }

  @Test
  public void when4X2HoursEvent_4reservedSlotsNoFreeSlots() {
    LocalDate day = LocalDate.of(2023, 10, 13);
    List<String> result = scheduleGenerator.generateDailySchedule(day, List.of(
        new Event(LocalDateTime.of(day, LocalTime.of(13, 0, 0)), LocalDateTime.of(day, LocalTime.of(15, 0, 0)), ScheduleGenerator.DUMMY_CREATOR),
        new Event(LocalDateTime.of(day, LocalTime.of(9, 0, 0)), LocalDateTime.of(day, LocalTime.of(11, 0, 0)), ScheduleGenerator.DUMMY_CREATOR),
        new Event(LocalDateTime.of(day, LocalTime.of(15, 0, 0)), LocalDateTime.of(day, LocalTime.of(17, 0, 0)), ScheduleGenerator.DUMMY_CREATOR),
        new Event(LocalDateTime.of(day, LocalTime.of(11, 0, 0)), LocalDateTime.of(day, LocalTime.of(13, 0, 0)), ScheduleGenerator.DUMMY_CREATOR)
    ));
    assertEquals(0, result.stream().filter(s -> s.contains(ScheduleGenerator.FREE_SLOT)).count());
    assertEquals(4, result.stream().filter(s -> s.contains(ScheduleGenerator.RESERVED_SLOT)).count());
  }

  @Test
  public void whenEventsAtBeginningAndEndOfDay_2reservedSlots1Free() {
    LocalDate day = LocalDate.of(2023, 10, 13);
    List<String> result = scheduleGenerator.generateDailySchedule(day, List.of(
        new Event(LocalDateTime.of(day, LocalTime.of(9, 0, 0)), LocalDateTime.of(day, LocalTime.of(11, 0, 0)), ScheduleGenerator.DUMMY_CREATOR),
        new Event(LocalDateTime.of(day, LocalTime.of(15, 0, 0)), LocalDateTime.of(day, LocalTime.of(17, 0, 0)), ScheduleGenerator.DUMMY_CREATOR)
    ));
    assertEquals(1, result.stream().filter(s -> s.contains(ScheduleGenerator.FREE_SLOT)).count());
    assertEquals(2, result.stream().filter(s -> s.contains(ScheduleGenerator.RESERVED_SLOT)).count());
  }

  @Test
  public void when1EventInTheMiddleOfTheDay_2FreeSlotsAnd1Reserved() {
    LocalDate day = LocalDate.of(2023, 10, 13);
    List<String> result = scheduleGenerator.generateDailySchedule(day, List.of(
        new Event(LocalDateTime.of(day, LocalTime.of(11, 0, 0)), LocalDateTime.of(day, LocalTime.of(13, 0, 0)), ScheduleGenerator.DUMMY_CREATOR)
    ));
    assertEquals(2, result.stream().filter(s -> s.contains(ScheduleGenerator.FREE_SLOT)).count());
    assertEquals(1, result.stream().filter(s -> s.contains(ScheduleGenerator.RESERVED_SLOT)).count());
  }

}
