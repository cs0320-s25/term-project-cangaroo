package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// profile will recommend 10 events to you (or all events depending on whichever is less)
public class RandomMatch {

  public RandomMatch() {}

  /*
   * @param allEvents - all events in the database
   * @param storageHandler - an object allowing you to use firebase utilities
   * @param profileID - the ID of the profile whose friends you are trying to rank events by friend attendance
   * @returns: list of event IDs of 10 or all events (whichever is less) completely at random
   */
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
