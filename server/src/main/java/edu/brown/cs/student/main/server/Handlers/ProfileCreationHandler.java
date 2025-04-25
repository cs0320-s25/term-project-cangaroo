package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProfileCreationHandler implements Route {
  public StorageInterface storageHandler;

  public ProfileCreationHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String uid = request.queryParams("uid");
    String interestedTags = request.queryParams("interestedTags");
    if ((uid == null) || (interestedTags == null)) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing required parameters: uid, interestedTags");
      return Utils.toMoshiJson(responseMap);
    }
    List<String> tags = Arrays.asList(interestedTags.trim().split(","));

    Map<String, Object> data = new HashMap<>();
    data.put("interestedTags", tags);
    data.put("friendsList", new ArrayList<>());
    data.put("eventsAttending", new ArrayList<>());

    this.storageHandler.addDocument(uid, "profile", "profileProperties", data);
    responseMap.put("result", "success");
    return Utils.toMoshiJson(responseMap);
  }
}
