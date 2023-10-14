package com.koszczi.calendar.model.event;

import com.koszczi.calendar.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "event_year_week", columnList = "year, week"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

  @Column(name = "id")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "year")
  private int year;

  @Column(name = "week")
  private int week;

  @Column(name = "start")
  private LocalTime start;

  @Column(name = "end")
  private LocalTime end;

  public Event(int year, int week, LocalTime start, LocalTime end) {
    this.year = year;
    this.week = week;
    this.start = start;
    this.end = end;
  }

}
