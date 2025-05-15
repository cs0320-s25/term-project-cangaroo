package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that sends a friend request */
public class SendFriendRequestHandler implements Route {
  // database object
  public FriendsStorage storageHandler;

  /**
   * Handler that sends a friend request
   *
   * @param storageHandler - a FriendsStorage object that has the Firestore object
   */
  public SendFriendRequestHandler(FriendsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String senderID = request.queryParams("senderID");
    String receiverID = request.queryParams("receiverID");

    if (senderID == null || receiverID == null) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing parameters: uid and friendID");
      return Utils.toMoshiJson(responseMap);
    }

    // send friend a friend request
    try {
      this.storageHandler.sendFriendRequest(senderID, receiverID);
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    }

    return Utils.toMoshiJson(responseMap);
  }
}
