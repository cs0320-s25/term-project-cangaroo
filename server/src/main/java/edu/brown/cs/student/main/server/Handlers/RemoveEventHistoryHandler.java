package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that removes an event from a user's history */
public class RemoveEventHistoryHandler implements Route {
  // database object
  public ProfileStorage storageHandler;

  /**
   * Handler that removes an event from a user's history
   *
   * @param storageHandler - a ProfileStorage object that has the Firestore object
   */
  public RemoveEventHistoryHandler(ProfileStorage storageHandler) {
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
      this.storageHandler.removeEventHistory(uid, eventID);
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile doesn't exist.");
    }
    return Utils.toMoshiJson(responseMap);
  }
}
