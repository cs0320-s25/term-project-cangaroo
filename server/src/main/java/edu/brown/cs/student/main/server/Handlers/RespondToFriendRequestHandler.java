package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoExistingFriendRequestException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler that responds to a friend request
 */
public class RespondToFriendRequestHandler implements Route {
  // database object
  public FriendsStorage storageHandler;

  /**
   * Handler that responds to a friend request
   * @param storageHandler - a FriendsStorage object that has the Firestore object
   */
  public RespondToFriendRequestHandler(FriendsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String senderID = request.queryParams("senderID");
    String receiverID = request.queryParams("receiverID");
    String isAccepted = request.queryParams("isAccepted");
    if (senderID == null
        || receiverID == null
        || (!isAccepted.equalsIgnoreCase("true") && !isAccepted.equalsIgnoreCase("false"))) {
      responseMap.put("result", "success");
      responseMap.put(
          "error_message",
          "Missing required parameters: senderID, receiverID, isAccepted (boolean)");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.storageHandler.respondToFriendRequest(
          senderID, receiverID, Boolean.parseBoolean(isAccepted));
      responseMap.put("result", "success");
      responseMap.put("requestAccepted", isAccepted);
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    } catch (NoExistingFriendRequestException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Friend request between the two profiles does not exist.");
    }
    return Utils.toMoshiJson(responseMap);
  }
}
