package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MostAttendedEventsByFriends {

  public MostAttendedEventsByFriends() {}

  /*
   * input: list of your friends (from your profile), list of all events
   * output: list of event IDs in order of most attended events by friends
   *
   * this method should go through the list of friends from your profile and compare them against
   * an event attendee list for every single event. Then, it should return every event id ranked by
   * the number of your friends attending.
   */
  public List<Integer> rankEventsWithFriends(StorageInterface storageHandler, String profileID)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    List<String> profileFriends =
        (List<String>) storageHandler.getProfile(profileID).get("friendsList");
    List<Map<String, Object>> allEvents = storageHandler.getAllEventsMap();

    Map<Integer, Integer> eventFriendCounts = new HashMap<>();

    for (Map<String, Object> event : allEvents) {
      List<String> attendees = (List<String>) event.get("usersAttending");
      Long eventIDLong = (Long) event.get("ID");
      int eventID = eventIDLong != null ? eventIDLong.intValue() : -1;

      int count = 0;
      for (String friend : profileFriends) {
        if (attendees.contains(friend)) {
          count++;
        }
      }

      eventFriendCounts.put(eventID, count);
    }

    return eventFriendCounts.entrySet().stream()
        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
        .limit(50)
        .map(Map.Entry::getKey)
        .toList();
  }
}
