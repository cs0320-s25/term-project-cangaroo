package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that returns all of a user's friends */
public class ViewFriendsHandler implements Route {
  // database object
  public FriendsStorage storageHandler;

  /**
   * Handler that returns all of a user's friends
   *
   * @param storageHandler - a FriendsStorage object that has the Firestore object
   */
  public ViewFriendsHandler(FriendsStorage storageHandler) {
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
      Map<String, String> friendData = this.storageHandler.viewFriends(uid);
      responseMap.put("result", "success");
      responseMap.put("friends", friendData);
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    } catch (Exception e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Internal server error: " + e.getMessage());
      e.printStackTrace();
    }
    return Utils.toMoshiJson(responseMap);
  }
}
