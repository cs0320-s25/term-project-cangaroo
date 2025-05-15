package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.HandlerLogic.RandomMatch;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

// handler processes the api requests for recommending events randomly
public class RandomRecommendHandler implements Route {

  Event e1 =
      new Event(
          List.of("Make", "Dinner"),
          List.of("Let's", "meet", "to", "practice", "culinary", "skills", "in", "the", "kitchen"),
          "2025-06-10",
          "08:00",
          "10:00",
          List.of("food", "health"),
          1,
          null);

  Event e2 =
      new Event(
          List.of("Book", "Club"),
          List.of("Discussing", "novels"),
          "2025-06-11",
          "17:00",
          "10:00",
          List.of("reading"),
          2,
          null);

  Event e3 =
      new Event(
          List.of("Sprint", "Practice"),
          List.of("Athletic", "running", "drills"),
          "2025-06-12",
          "10:00",
          "10:00",
          List.of("track"),
          3,
          null);

  private Map<String, Object> responseMap;
  public ProfileStorage storageHandler;

  public RandomRecommendHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.responseMap = new HashMap<>();
    RandomMatch randomMatch = new RandomMatch();
    List<String> results = randomMatch.getRandomEvent(this.storageHandler.getAllEvents());
    // no events matched
    if (results == null || results.isEmpty()) {
      this.responseMap.put("result", "Success");
      this.responseMap.put(
          "error_message", "No events found. There are no events in the database.");
      this.responseMap.put("event_ids", results);
      return Utils.toMoshiJson(this.responseMap);
    }

    // successful result
    this.responseMap.put("result", "Success");
    this.responseMap.put("event_ids", results);
    return Utils.toMoshiJson(this.responseMap);
  }
}
