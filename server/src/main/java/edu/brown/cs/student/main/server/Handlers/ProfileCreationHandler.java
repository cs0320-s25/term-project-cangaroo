package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that creates a profile */
public class ProfileCreationHandler implements Route {
  // database object
  public ProfileStorage storageHandler;

  /**
   * Handler that creates a profile
   *
   * @param storageHandler - a ProfileStorage object that has the Firestore object
   */
  public ProfileCreationHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String uid = request.queryParams("uid");
    String interestedTags = request.queryParams("interestedTags");
    String favEventOrganizerString = request.queryParams("favEventOrganizers");
    String profilePicUrl = request.queryParams("profilePicUrl");

    String username = request.queryParams("username");
    if ((uid == null)
        || (interestedTags == null)
        || (username == null)
        || (favEventOrganizerString == null)
        || (profilePicUrl == null)) {

      responseMap.put("result", "failure");
      responseMap.put(
          "error_message",
          "Missing required parameters: uid, interestedTags, favEventOrganizers, profilePicUrl, username");
      return Utils.toMoshiJson(responseMap);
    }
    List<String> tags = Arrays.asList(interestedTags.trim().split(","));
    List<String> favEventOrganizers = Arrays.asList(favEventOrganizerString.trim().split(","));

    Map<String, Object> data = new HashMap<>();
    data.put("username", username);
    data.put("interestedTags", tags);
    data.put("friendsList", new ArrayList<>());
    data.put("eventsAttending", new ArrayList<>());
    data.put("interestedOrganizations", favEventOrganizers);
    data.put("eventHistory", new ArrayList<>());
    data.put("profilePicUrl", profilePicUrl);

    //    this.storageHandler.addDocument(uid, "profile", "profileProperties", data);
    this.storageHandler.addProfile(uid, data);
    responseMap.put("result", "success");
    return Utils.toMoshiJson(responseMap);
  }
}
