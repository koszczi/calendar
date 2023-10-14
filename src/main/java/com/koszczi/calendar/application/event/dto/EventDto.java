package com.koszczi.calendar.application.event.dto;

import java.time.LocalDateTime;

public record EventDto(LocalDateTime startDateTime, LocalDateTime endDateTime, String organizer) { }
