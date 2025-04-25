package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewEventHandler implements Route {

  public StorageInterface storageHandler;

  public ViewEventHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");
      String eventID = request.queryParams("eventID");
      if ((uid == null) || (eventID == null)) {
        responseMap.put("result", "failure");
        responseMap.put("error_message", "Missing required parameters.");
        return Utils.toMoshiJson(responseMap);
      }

      Map<String, Object> data = this.storageHandler.getEvent(uid, eventID);
      System.out.println(data);
      responseMap.put("result", "success");
      responseMap.put("data", data);

    } catch (NoEventFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Event does not exist.");
    }

    // use storage handler to get the event data (as one map object)
    // then, json string it? and then put in responseMap
    return Utils.toMoshiJson(responseMap);
  }
}
