package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Storage.EventsStorage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class EditEventHandler implements Route {
  public EventsStorage storageHandler;

  public EditEventHandler(EventsStorage storageHandler) {
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
    String eventID = request.queryParams("eventID");
    String eventOrganizer = request.queryParams("eventOrganizer");
    String thumbnailUrl = request.queryParams("thumbnailUrl");
    List<String> tags = Arrays.asList(tagsString.trim().split(","));

    if ((uid == null)
        || (name == null)
        || (description == null)
        || (date == null)
        || (startTime == null)
        || (endTime == null)
        || (tags.isEmpty())
        || (eventID == null)
        || (eventOrganizer == null)
        || (thumbnailUrl == null)) {

      responseMap.put("status", "failure");
      responseMap.put("error_message", "Missing parameters");
      return Utils.toMoshiJson(responseMap);
    }
    try {
      this.storageHandler.editEvent(
          uid,
          eventID,
          name,
          description,
          date,
          startTime,
          endTime,
          tags,
          eventOrganizer,
          thumbnailUrl);
      responseMap.put("result", "success");
      responseMap.put("eventID", eventID);
    } catch (NoEventFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Event does not exist.");
    } catch (Exception e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return Utils.toMoshiJson(responseMap);
  }
}
