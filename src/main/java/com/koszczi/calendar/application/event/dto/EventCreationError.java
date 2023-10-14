package com.koszczi.calendar.application.event.dto;

public record EventCreationError(String errorCode, String errorMessage) {
  public static EventCreationError of(ValidationError validationError) {
    return new EventCreationError(validationError.name(), validationError.getDefaultMessage());
  }
}