package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.HandlerLogic.MatchEvents;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

// handler processes the api requests for event-profile matching
public class RecommendHandler implements Route {

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

  List<String> mockedTags = List.of("cooking");

  public ProfileStorage storageHandler;
  private Map<String, Object> responseMap;

  public RecommendHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {

    String input = request.queryParams("profileID");

    // null input
    if (input == null || input.isEmpty()) {
      this.responseMap.put("result", "Error: No input given.");
      this.responseMap.put("error_message", "No matches due to error");
      this.responseMap.put("event_ids", new ArrayList<>());
      return Utils.toMoshiJson(this.responseMap);
    }

    this.responseMap = new HashMap<>();
    MatchEvents matchEvents = new MatchEvents();
    List<String> tags = (List<String>) this.storageHandler.getProfile(input).get("interestedTags");
    List<String> interestedOrgs =
        (List<String>) this.storageHandler.getProfile(input).get("interestedOrganizations");
    List<Integer> results =
        matchEvents.getMatchedEvents(tags, interestedOrgs, this.storageHandler.getAllEvents());

    // no events matched
    if (results == null || results.isEmpty()) {
      this.responseMap.put("result", "Success");
      this.responseMap.put("error_message", "No events matched. There are no events to recommend.");
      this.responseMap.put("event_ids", results);
      return Utils.toMoshiJson(this.responseMap);
    }

    // successful result
    this.responseMap.put("result", "Success");
    this.responseMap.put("event_ids", results);
    return Utils.toMoshiJson(this.responseMap);
  }
}
