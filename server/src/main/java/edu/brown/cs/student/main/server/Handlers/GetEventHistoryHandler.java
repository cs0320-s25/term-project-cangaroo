package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that gets the event history of a user */
public class GetEventHistoryHandler implements Route {
  // database object
  public ProfileStorage storageHandler;

  /**
   * Handler that gets the event history of a user
   *
   * @param storageHandler - a ProfileStorage object that has the Firestore object
   */
  public GetEventHistoryHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String uid = request.queryParams("uid");
    if (uid == null) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing required parameter: uid");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      List<Map<String, Object>> eventHistory = this.storageHandler.getEventHistory(uid);
      responseMap.put("result", "success");
      responseMap.put("data", eventHistory);
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile doesn't exist.");
    }

    return Utils.toMoshiJson(responseMap);
  }
}
