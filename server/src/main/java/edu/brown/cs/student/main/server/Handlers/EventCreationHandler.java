package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class EventCreationHandler implements Route {
  public StorageInterface storageHandler;

  public EventCreationHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      String name = request.queryParams("name");
      String description = request.queryParams("description");
      String date = request.queryParams("date");
      String startTime = request.queryParams("startTime");
      String endTime = request.queryParams("endTime");
      String tagsString = request.queryParams("tags");
      String eventOrganizer = request.queryParams("eventOrganizer");

      if ((uid == null)
          || (name == null)
          || (description == null)
          || (date == null)
          || (startTime == null)
          || (endTime == null)
          || (tagsString == null)
          || (eventOrganizer == null)){
        responseMap.put("result", "failure");
        responseMap.put("error_message", "Missing required parameters");
        return Utils.toMoshiJson(responseMap);
      }

      List<String> tags = Arrays.asList(tagsString.trim().split(","));
      Map<String, Object> data = new HashMap<>();

      int eventID = this.storageHandler.getCurrEventID();

      data.put("name", name);
      data.put("description", description);
      data.put("date", date);
      data.put("startTime", startTime);
      data.put("endTime", endTime);
      data.put("tags", tags);
      data.put("ID", eventID);
      data.put("eventOrganizer", eventOrganizer);

      this.storageHandler.addDocument(uid, "events", "event-" + eventID, data);
      this.storageHandler.updateEventID(eventID + 1);

      responseMap.put("result", "success");
      responseMap.put("eventID", eventID);
      return Utils.toMoshiJson(responseMap);
    } catch (Exception e) {
      // change later
      responseMap.put("result", "failure");
      responseMap.put("error_message", e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}

// frontend passes in event details -> store separately in firestore
// when user wants to get an event, return the document (map) that contains the event details
