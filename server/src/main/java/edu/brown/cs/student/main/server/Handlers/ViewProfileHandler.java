package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewProfileHandler implements Route {

  public StorageInterface storageHandler;

  public ViewProfileHandler(StorageInterface storageHandler) {
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
      responseMap.put("data", this.storageHandler.getProfile(uid));
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    } catch (Exception e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", e.getMessage());
      e.printStackTrace();
    }

    return Utils.toMoshiJson(responseMap);
  }
}
