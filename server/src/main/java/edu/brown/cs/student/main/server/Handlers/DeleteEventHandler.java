package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Storage.EventsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteEventHandler implements Route {

  public EventsStorage storageHandler;

  public DeleteEventHandler(EventsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      String eventID = request.queryParams("eventID");
      if (eventID == null || uid == null) {
        responseMap.put("result", "failure");
        responseMap.put("error_message", "Missing required parameters: uid, eventID");
        return Utils.toMoshiJson(responseMap);
      }
      this.storageHandler.deleteEvent(uid, eventID);
      responseMap.put("result", "success");
      responseMap.put("eventID", eventID);
    } catch (NoEventFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Event does not exist.");
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error_message", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
