package com.koszczi.calendar.application.event;

import com.koszczi.calendar.model.event.Event;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class ScheduleGenerator {

  public static final String DUMMY_CREATOR = "DOES NOT MATTER";
  public static final String FREE_SLOT = "Free slot between ";
  public static final String RESERVED_SLOT = "Reserved slot between ";
  private static final LocalTime EIGHT_AM = LocalTime.of(8, 0, 0);
  private static final LocalTime NINE_AM = LocalTime.of(9, 0, 0);
  private static final LocalTime FIVE_PM = LocalTime.of(17, 0, 0);
  private static final LocalTime SIX_PM = LocalTime.of(18, 0, 0);

  public List<String> generateDailySchedule(LocalDate day, Collection<Event> dailyEvents) {
    List<String> result = new ArrayList<>();
    List<Event> orderedEvents = getOrderedListOfEventsWithDummyAtMorningAndEvening(day, dailyEvents);
    for (int i = 1; i < orderedEvents.size(); i++) {
      if (!orderedEvents.get(i - 1).getEnd().equals(orderedEvents.get(i).getStart())) {
        result.add((new StringBuilder()).append(FREE_SLOT)
            .append(orderedEvents.get(i - 1).getEnd())
            .append(" and ")
            .append(orderedEvents.get(i).getStart())
            .toString()
        );
      }
      if (i == orderedEvents.size() - 1) {
        break;
      }
      else {
        result.add((new StringBuilder()).append(RESERVED_SLOT)
            .append(orderedEvents.get(i).getStart())
            .append(" and ")
            .append(orderedEvents.get(i).getEnd())
            .toString()
        );
      }
    }
    return result;
  }

  private List<Event> getOrderedListOfEventsWithDummyAtMorningAndEvening(LocalDate day, Collection<Event> rawEventCollection) {
    List<Event> events = new ArrayList<>(rawEventCollection.size() + 2);
    LocalDateTime morningDummyEventStart = LocalDateTime.of(day, EIGHT_AM);
    LocalDateTime morningDummyEventEnd = LocalDateTime.of(day, NINE_AM);
    events.add(new Event(morningDummyEventStart, morningDummyEventEnd, DUMMY_CREATOR));

    events.addAll(rawEventCollection.stream().sorted(Comparator.comparing(Event::getStart)).toList());

    LocalDateTime eveningDummyEventStart = LocalDateTime.of(day, FIVE_PM);
    LocalDateTime eveningDummyEventEnd = LocalDateTime.of(day, SIX_PM);
    events.add(new Event(eveningDummyEventStart, eveningDummyEventEnd, DUMMY_CREATOR));

    return events;
  }
}
