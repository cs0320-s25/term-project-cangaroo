package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.HandlerLogic.MostAttendedEventsByFriends;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class MostAttendedEventsFriendHandler implements Route {
  public ProfileStorage storageHandler;
  private Map<String, Object> responseMap;

  public MostAttendedEventsFriendHandler(ProfileStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.responseMap = new HashMap<>();
    String profileID = request.queryParams("profileID");

    if (profileID == null || profileID.isEmpty()) {
      this.responseMap.put("result", "Error: No profileID given.");
      return Utils.toMoshiJson(this.responseMap);
    }

    MostAttendedEventsByFriends myRanker = new MostAttendedEventsByFriends();

    List<Integer> results;
    try {
      results = myRanker.rankEventsWithFriends(this.storageHandler, profileID);
    } catch (Exception e) {
      this.responseMap.put("result", "Error");
      this.responseMap.put("message", e.getMessage());
      return Utils.toMoshiJson(this.responseMap);
    }

    this.responseMap.put("result", "Success");
    this.responseMap.put("event_ids", results);
    return Utils.toMoshiJson(this.responseMap);
  }
}
