package com.koszczi.calendar.application.event.dto;

import lombok.Getter;

public enum ValidationError {
  EVENT_TOO_LONG("Event is too long"),
  INVALID_TIME("Events can start/end at XX:00:00 or XX:30:00"),
  NOT_WEEKDAY("Events can be created only for weekdays"),
  OUT_OF_TIMERANGE("Events can be created between 9:30 and 17:00"),
  OVERLAPPING_EVENTS("Event would overlap with other event(s)");

  @Getter
  private String defaultMessage;

  ValidationError(String defaultMessage) {
    this.defaultMessage = defaultMessage;
  }
}
