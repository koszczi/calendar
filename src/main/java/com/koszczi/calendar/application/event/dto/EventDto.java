package com.koszczi.calendar.application.event;

import java.time.LocalDateTime;

public record EventDto(LocalDateTime startDateTime, LocalDateTime endDateTime) { }
