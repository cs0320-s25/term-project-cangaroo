package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.HandlerLogic.Rate;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

//handler processes the api requests for rating events
public class RateHandler implements Route {

  public StorageInterface storageHandler;
  private Map<String, Object> responseMap;

  public RateHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.responseMap = new HashMap<>();

    String profileID = request.queryParams("profileID");
    String eventID = request.queryParams("eventID");
    String review = request.queryParams("review");

    if (review == null || review.isEmpty()) {
      this.responseMap.put("result", "Error: No review given.");
      return this.responseMap;
    }

    if (eventID == null || profileID == null || profileID.isEmpty() || eventID.isEmpty()) {
      this.responseMap.put("result", "Error: No eventID or profileID given.");
      return this.responseMap;
    }

    Boolean myReview = Boolean.parseBoolean(review);
    Rate myRate = new Rate();

    try {
      myRate.rateEvent(this.storageHandler, profileID, eventID, myReview);
    } catch (Exception e) {
      this.responseMap.put("result", "Error");
      this.responseMap.put("message", e.getMessage());
      return this.responseMap;
    }

    this.responseMap.put("result", "Success");
    this.responseMap.put("profile", this.storageHandler.getProfile(profileID));
    return this.responseMap;
  }
}
