package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetOutgoingFriendRequests implements Route {
  public StorageInterface storageHandler;

  public GetOutgoingFriendRequests(StorageInterface storageHandler) {
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
      Map<String, String> friendRequests = this.storageHandler.getFriendRequests(uid, true);
      responseMap.put("result", "success");
      responseMap.put("outgoingFriendRequests", friendRequests);
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
