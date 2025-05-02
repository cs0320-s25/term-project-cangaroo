package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.HandlerLogic.Search;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

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

  public SearchHandler() {}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.responseMap = new HashMap<>();

    Set<String> params = request.queryParams();
    String input = request.queryParams("input");

    // null input
    if (input == null || input.isEmpty()) {
      this.responseMap.put("result", "Error: No input given.");
      this.responseMap.put("error_message", "No matches due to error");
      this.responseMap.put("events", new ArrayList<>());
      return this.responseMap;
    }

    List<String> words = Arrays.asList(input.split("\\s+"));
    Search search = new Search();
    List<Event> results = search.getSearchedEvents(words, List.of(e1, e2, e3));

    // no events matched
    if (results == null || results.isEmpty()) {
      this.responseMap.put("result", "Success");
      this.responseMap.put("error_message", "No events found. Try searching something else.");
      this.responseMap.put("events", results);
      return this.responseMap;
    }

    // successful result
    this.responseMap.put("result", "Success");
    this.responseMap.put("events", results);
    return this.responseMap;
  }
}
