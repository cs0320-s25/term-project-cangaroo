package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.EventsStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler that creates an event
 */
public class EventCreationHandler implements Route {
  // database object
  public EventsStorage storageHandler;

  /**
   * Handler that creates an event
   * @param storageHandler - an EventsStorage object that has the Firestore object
   */
  public EventCreationHandler(EventsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    String uid = request.queryParams("uid");
    String name = request.queryParams("name");
    String description = request.queryParams("description");
    String date = request.queryParams("date");
    String startTime = request.queryParams("startTime");
    String endTime = request.queryParams("endTime");
    String tagsString = request.queryParams("tags");
    String eventOrganizer = request.queryParams("eventOrganizer");
    String thumbnailUrl = request.queryParams("thumbnailUrl");

    if ((uid == null)
        || (name == null)
        || (description == null)
        || (date == null)
        || (startTime == null)
        || (endTime == null)
        || (tagsString == null)
        || (eventOrganizer == null)
        || (thumbnailUrl == null)) {
      responseMap.put("result", "failure");
      responseMap.put(
          "error_message",
          "Missing required parameters: uid, name, description, date, startTime, endTime, tags, eventOrganizer, thumbnailUrl");
      return Utils.toMoshiJson(responseMap);
    }

    List<String> tags = Arrays.asList(tagsString.trim().split(","));
    Map<String, Object> data = new HashMap<>();

    int eventID = this.storageHandler.getCurrEventID();

    data.put("uid", uid);
    data.put("name", name);
    data.put("description", description);
    data.put("date", date);
    data.put("startTime", startTime);
    data.put("endTime", endTime);
    data.put("tags", tags);
    data.put("ID", eventID);
    data.put("eventOrganizer", eventOrganizer);
    data.put("usersAttending", new ArrayList<>());
    data.put("thumbnailUrl", thumbnailUrl);

    try {
      this.storageHandler.addEvent(uid, eventID, data);
      this.storageHandler.updateEventID(eventID + 1);
      responseMap.put("result", "success");
      responseMap.put("event", data);
      responseMap.put("eventID", eventID);
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile doesn't exist.");
    }

    return Utils.toMoshiJson(responseMap);
  }
}
