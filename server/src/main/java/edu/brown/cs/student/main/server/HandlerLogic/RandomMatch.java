package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomMatch {

  public RandomMatch() {}

  public List<String> getRandomEvent(List<Event> allEvents) {
    int numberOfEvents = Math.min(allEvents.size(), 10);

    List<Event> copy = new ArrayList<>(allEvents); // Don't mutate original list
    Collections.shuffle(copy);
    List<Event> randomEvents = copy.subList(0, numberOfEvents);

    List<String> eventIDs = new ArrayList<>();
    for (Event event : randomEvents) {
      eventIDs.add(Integer.toString(event.eventID()));
    }
    return eventIDs;
  }
}
