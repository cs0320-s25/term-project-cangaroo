package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// recommends events solely based on how many friends are attending
public class MostAttendedEventsByFriends {

  public MostAttendedEventsByFriends() {}

  /*
   * input:
   * @param storageHandler - an object allowing you to use firebase utilities
   * @param profileID - the ID of the profile whose friends you are trying to rank events by friend attendance
   * @returns: list of event IDs in order of most attended events by friends
   */
  public List<Integer> rankEventsWithFriends(ProfileStorage storageHandler, String profileID)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    // get the list of friends
    List<String> profileFriends =
        (List<String>) storageHandler.getProfile(profileID).get("friendsList");
    List<Map<String, Object>> allEvents = storageHandler.getAllEventsMap();

    Map<Integer, Integer> eventFriendCounts = new HashMap<>();

    // for each event
    for (Map<String, Object> event : allEvents) {
      // get users attending every event
      List<String> attendees = (List<String>) event.get("usersAttending");
      Long eventIDLong = (Long) event.get("ID");
      int eventID = eventIDLong != null ? eventIDLong.intValue() : -1;

      // tally up how many friends are attending
      int count = 0;
      for (String friend : profileFriends) {
        if (attendees.contains(friend)) {
          count++;
        }
      }

      // allows you to display every event where your friend(s) is/are attending
      if (count > 0) {
        eventFriendCounts.put(eventID, count);
      }
    }

    // organizes display order by events with most friends attending
    return eventFriendCounts.entrySet().stream()
        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
        .limit(50)
        .map(Map.Entry::getKey)
        .toList();
  }
}
