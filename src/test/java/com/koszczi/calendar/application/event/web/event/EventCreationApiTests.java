package com.koszczi.calendar.application.event.web.event;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.koszczi.calendar.application.event.dto.EventCreationResult;
import com.koszczi.calendar.application.event.dto.EventDto;
import com.koszczi.calendar.model.event.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.koszczi.calendar.application.event.dto.EventCreationStatus.VALIDATION_FAILURE;
import static com.koszczi.calendar.application.event.dto.ValidationError.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventCreationApiTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private EventRepository eventRepository;

  private JsonMapper jsonMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

  private static final String EVENT_CREATION_PATH = "/events";
  private static final String ORGANIZER = "organizer";
  @Test
  public void whenEventTooLong_BadRequest() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 2, 10, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 2, 13, 30, 00);
    EventDto input = new EventDto(eventStart, eventEnd, ORGANIZER);

    MvcResult result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(400, result.getResponse().getStatus());
    EventCreationResult responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertEquals(VALIDATION_FAILURE, responseBody.status());
    assertNull(responseBody.newEvent());
    assertTrue(responseBody.overlappingEvents().isEmpty());
    assertEquals(1, responseBody.errors().size());
    assertTrue(responseBody.errors().stream().allMatch(e -> EVENT_TOO_LONG.name().equals(e.errorCode())));
  }

  @Test
  public void whenEventStartDateIsInvalid_BadRequest() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 2, 10, 10, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 2, 11, 30, 00);
    EventDto input = new EventDto(eventStart, eventEnd, ORGANIZER);

    MvcResult result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(400, result.getResponse().getStatus());
    EventCreationResult responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertEquals(VALIDATION_FAILURE, responseBody.status());
    assertNull(responseBody.newEvent());
    assertTrue(responseBody.overlappingEvents().isEmpty());
    assertEquals(1, responseBody.errors().size());
    assertTrue(responseBody.errors().stream().allMatch(e -> INVALID_TIME.name().equals(e.errorCode())));
  }

  @Test
  public void whenEventStartsTooEarly_BadRequest() throws Exception {
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 2, 8, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 2, 10, 30, 00);
    EventDto input = new EventDto(eventStart, eventEnd, ORGANIZER);

    MvcResult result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(400, result.getResponse().getStatus());
    EventCreationResult responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertEquals(VALIDATION_FAILURE, responseBody.status());
    assertNull(responseBody.newEvent());
    assertTrue(responseBody.overlappingEvents().isEmpty());
    assertEquals(1, responseBody.errors().size());
    assertTrue(responseBody.errors().stream().allMatch(e -> OUT_OF_TIMERANGE.name().equals(e.errorCode())));
  }

  @Test
  public void whenNewEventOverlapsAnExisting_BadRequest() throws Exception {
    // first event can be created
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 3, 9, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 3, 10, 30, 00);
    EventDto input = new EventDto(eventStart, eventEnd, ORGANIZER);

    MvcResult result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    EventCreationResult responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertNotNull(responseBody.newEvent());

    // second event would overlap the first one
    eventStart = LocalDateTime.of(2023, 10, 3, 10, 00, 00);
    eventEnd = LocalDateTime.of(2023, 10, 3, 12, 30, 00);
    input = new EventDto(eventStart, eventEnd, ORGANIZER);

    result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(400, result.getResponse().getStatus());
    responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertEquals(VALIDATION_FAILURE, responseBody.status());
    assertNull(responseBody.newEvent());
    assertEquals(1, responseBody.overlappingEvents().size());
    assertEquals(1, responseBody.errors().size());
    assertTrue(responseBody.errors().stream().allMatch(e -> OVERLAPPING_EVENTS.name().equals(e.errorCode())));
  }

  @Test
  public void whenEventsDontOverlapAndAll2hoursLong_4newEventsCanBeCreated() throws Exception {
    // event #1
    LocalDateTime eventStart = LocalDateTime.of(2023, 10, 4, 9, 00, 00);
    LocalDateTime eventEnd = LocalDateTime.of(2023, 10, 4, 11, 00, 00);
    EventDto input = new EventDto(eventStart, eventEnd, ORGANIZER);

    MvcResult result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    EventCreationResult responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertNotNull(responseBody.newEvent());

    // event #2
    eventStart = LocalDateTime.of(2023, 10, 4, 11, 00, 00);
    eventEnd = LocalDateTime.of(2023, 10, 4, 13, 00, 00);
    input = new EventDto(eventStart, eventEnd, ORGANIZER);

    result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertNotNull(responseBody.newEvent());

    // event #3
    eventStart = LocalDateTime.of(2023, 10, 4, 13, 00, 00);
    eventEnd = LocalDateTime.of(2023, 10, 4, 15, 00, 00);
    input = new EventDto(eventStart, eventEnd, ORGANIZER);

    result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertNotNull(responseBody.newEvent());

    // event #14
    eventStart = LocalDateTime.of(2023, 10, 4, 15, 00, 00);
    eventEnd = LocalDateTime.of(2023, 10, 4, 17, 00, 00);
    input = new EventDto(eventStart, eventEnd, ORGANIZER);

    result = mvc
        .perform(post(EVENT_CREATION_PATH).contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(input)))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    responseBody = jsonMapper.readValue(result.getResponse().getContentAsString(), EventCreationResult.class);
    assertNotNull(responseBody.newEvent());
  }

}
