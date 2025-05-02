package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Rate {

  public Rate() {}

  /*
   * this method should append the eventorganizer of an event that a profile really likes
   * to the profile's favorite organizations
   */
  public void rateEvent(
      StorageInterface storageHandler, String profileID, String eventID, Boolean likedEvent)
      throws ExecutionException,
          InterruptedException,
          NoEventFoundException,
          NoProfileFoundException {

    if (likedEvent) {
      String eventOrganizer = (String) storageHandler.getEvent(eventID).get("eventOrganizer");
      String profileFavOrganizers =
          (String) storageHandler.getProfile(profileID).get("interestedOrganizations");
      String tags = (String) storageHandler.getEvent(eventID).get("tags");

      List<String> tagList;
      List<String> profileFavOrganizersList;

      if (tags != null && !tags.isEmpty()) {
        tagList = Arrays.asList(tags.split(","));
      } else {
        tagList = new ArrayList<>();
      }

      if (profileFavOrganizers != null && !profileFavOrganizers.isEmpty()) {
        profileFavOrganizersList = Arrays.asList(profileFavOrganizers.split(","));
        profileFavOrganizersList.add(eventOrganizer);
      } else {
        profileFavOrganizersList = new ArrayList<>();
        profileFavOrganizersList.add(eventOrganizer);
      }

      storageHandler.editProfile(profileID, tagList, profileFavOrganizersList);
    }
  }
}
