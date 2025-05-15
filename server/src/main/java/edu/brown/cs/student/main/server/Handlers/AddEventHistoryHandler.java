package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddEventHistoryHandler implements Route {
  //  public StorageInterface storageHandler;

  public ProfileStorage storageHandler;

  public AddEventHistoryHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String uid = request.queryParams("uid");
    String eventID = request.queryParams("eventID");

    if (uid == null || eventID == null) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing required parameters: uid, eventID");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.storageHandler.addEventHistory(uid, eventID);
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile doesn't exist.");
    } catch (NoEventFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Event doesn't exist.");
    }

    return Utils.toMoshiJson(responseMap);
  }
}
