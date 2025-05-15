package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class UnsendFriendRequestHandler implements Route {

  public FriendsStorage storageHandler;

  public UnsendFriendRequestHandler(FriendsStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String senderID = request.queryParams("senderID");
    String receiverID = request.queryParams("receiverID");
    if (senderID == null || receiverID == null) {
      responseMap.put("result", "success");
      responseMap.put("error_message", "Missing required parameters: senderID, receiverID");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.storageHandler.unsendFriendRequest(senderID, receiverID);
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    }
    return Utils.toMoshiJson(responseMap);
  }
}
