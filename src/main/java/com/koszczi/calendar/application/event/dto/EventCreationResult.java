package com.koszczi.calendar.application.event.dto;

import com.koszczi.calendar.model.event.Event;

import java.util.Collection;

public record EventCreationResult(EventCreationStatus status, Collection<ValidationError> errors,
                                  Collection<String> overlappingEvents, Event newEvent) { }
