package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//allows you to rate an event which will update the algorithm if you enjoyed it
public class Rate {

  public Rate() {}

  /*
   * this method should append the eventorganizer of an event that a profile really likes
   * to the profile's favorite organizations
   *
   * param storageHandler - an object allowing you to use firebase utilities
   * @param profileID - the ID of the profile of the rater
   * @param eventID - the event the person is rating
   * @param likedEvent - whether or not someone liked the event
   */
  public void rateEvent(
      StorageInterface storageHandler, String profileID, String eventID, Boolean likedEvent)
      throws ExecutionException,
          InterruptedException,
          NoEventFoundException,
          NoProfileFoundException {

    //if you liked the event
    if (likedEvent) {
      String eventOrganizer = (String) storageHandler.getEvent(eventID).get("eventOrganizer");
      List<String> profileFavOrganizers =
          (List<String>) storageHandler.getProfile(profileID).get("interestedOrganizations");
      List<String> tagsList = (List<String>) storageHandler.getEvent(eventID).get("tags");

      if (profileFavOrganizers != null && !profileFavOrganizers.isEmpty()) {

        if (!profileFavOrganizers.contains(eventOrganizer)) {
          profileFavOrganizers.add(eventOrganizer);
        }
      } else {
        profileFavOrganizers = new ArrayList<>();
        profileFavOrganizers.add(eventOrganizer);
      }

      //edit the profiile to add the fav event organizer
      storageHandler.editProfile(profileID, tagsList, profileFavOrganizers, null);
    }
  }
}
