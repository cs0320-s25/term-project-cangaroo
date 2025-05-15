package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Exceptions.NotFriendsException;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler that unfriends two users
 */
public class UnfriendHandler implements Route {
  // database object
  public FriendsStorage storageHandler;

  /**
   * Handler that unfriends two users
   * @param storageHandler - a FriendsStorage object that has the Firestore object
   */
  public UnfriendHandler(FriendsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String user1 = request.queryParams("user1");
    String user2 = request.queryParams("user2");

    if (user1 == null || user2 == null) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing required parameters: user1, user2");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.storageHandler.removeFriends(user1, user2);
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    } catch (NotFriendsException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Users are not friends in the first place: Can't unfriend.");
    }
    return Utils.toMoshiJson(responseMap);
  }
}
