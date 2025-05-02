package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Profiles.Profile;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Rate {

  public Rate() {
  }

  /*
   * input: event, profile, whether you liked the event/not
   * output: add event organizer if you do
   */

  public void rateEvent(StorageInterface storageHandler, String profileID, String eventUID, String eventID, Boolean likedEvent)
      throws ExecutionException, InterruptedException, NoEventFoundException, NoProfileFoundException {
    if (likedEvent) {
      //ask if storageHandler should
      String eventOrganizer = (String) storageHandler.getEvent(eventUID, eventID).get("eventOrganizer");
      storageHandler.editProfile(profileID, null, List.of(eventOrganizer));

    }
  }
}
