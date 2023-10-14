package com.koszczi.calendar.model.event;

import com.koszczi.calendar.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "event_year_week", columnList = "year, week"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class Event extends BaseEntity {

  @Column(name = "id")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "year")
  private int year;

  @Column(name = "week")
  private int week;

  @Column(name = "day_of_week")
  private DayOfWeek dayOfWeek;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "start")
  private LocalTime start;

  @Column(name = "end")
  private LocalTime end;

  public Event(LocalDateTime start, LocalDateTime end) {
    this.year = start.getYear();
    TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    this.week = start.get(woy);
    this.date = start.toLocalDate();
    this.dayOfWeek = start.getDayOfWeek();
    this.start = start.toLocalTime();
    this.end = end.toLocalTime();
  }

}
