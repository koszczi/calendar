package com.koszczi.calendar.model.event;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
  public Iterable<Event> findAllByYearAndWeek(int year, int week);
}
