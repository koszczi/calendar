package com.koszczi.calendar.model.event;

import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

public interface EventRepository extends CrudRepository<Event, Long> {

  Collection<Event> findAllByDate(LocalDate date);
  Collection<Event> findAllByYearAndWeek(int year, int week);
}
