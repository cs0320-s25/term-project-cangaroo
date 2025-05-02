package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class EditProfileHandler implements Route {
  public StorageInterface storageHandler;

  public EditProfileHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    String uid = request.queryParams("uid");
    String tagsString = request.queryParams("interestedTags");
    String favEventOrganizersString = request.queryParams("eventOrganizer");

    if ((uid == null) || (tagsString == null) || (favEventOrganizersString == null)) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Missing required parameters: uid, interestedTags");
      return Utils.toMoshiJson(responseMap);
    }
    List<String> tags = Arrays.asList(tagsString.trim().split(","));
    List<String> favEventOrganizers = Arrays.asList(favEventOrganizersString.trim().split(","));
    try {
      this.storageHandler.editProfile(uid, tags, favEventOrganizers);
      responseMap.put("result", "success");
      responseMap.put("newTags", tags);
      responseMap.put("newFavEventOrganizer", favEventOrganizers);
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
