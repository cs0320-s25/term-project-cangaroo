package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.EventAlreadyAttendingException;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/** Class that manipulates profile data in the Firestore database */
public class ProfileStorage {
  // database object
  private Firestore db;

  /**
   * Manipulates profile data in the Firestore database
   *
   * @param db - the Firestore database created by GeneralStorage
   */
  public ProfileStorage(Firestore db) throws IOException {
    this.db = db;
  }

  /**
   * Retrieves a profile
   *
   * @param uid - clerkID of the profile
   * @return a Map<String, Object> containing the profile details
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoProfileFoundException - if profile not found
   */
  public Map<String, Object> getProfile(String uid)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    DocumentReference docRef = db.collection("users").document(uid);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot snapshot = future.get();

    if (snapshot.exists()) {
      return snapshot.getData();
    } else {
      throw new NoProfileFoundException("Profile does not exist");
    }
  }

  /**
   * Edits a profile's details
   *
   * @param uid - clerkID of the profile
   * @param tags - the tags of a user
   * @param favEventOrganizers - the user's favorite organizers
   * @param profilePicUrl - a url that links to their profile picture
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoProfileFoundException - if profile not found
   */
  public void editProfile(
      String uid, List<String> tags, List<String> favEventOrganizers, String profilePicUrl)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    DocumentReference docRef = db.collection("users").document(uid);
    if (docRef.get().get().exists()) {
      docRef.update("interestedTags", tags);
      docRef.update("interestedOrganizations", favEventOrganizers);
      docRef.update("profilePicUrl", profilePicUrl);
    } else {
      throw new NoProfileFoundException("No such profile.");
    }
  }

  /**
   * Gets all the events in the database
   *
   * @return All the events as a list of Event records
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public List<Event> getAllEvents() throws ExecutionException, InterruptedException {
    Iterable<DocumentReference> collection = db.collection("events").listDocuments();
    ArrayList<Event> events = new ArrayList<>();
    for (DocumentReference eventRef : collection) {
      DocumentSnapshot document = eventRef.get().get();

      //       Get name, description, and tags as List<String> directly
      String name = (String) document.get("name");
      String description = (String) document.get("description");
      List<String> tags = (List<String>) document.get("tags");

      List<String> listOfNames = Arrays.asList(name.trim().split("\\s+"));
      List<String> listOfDescriptions = Arrays.asList(description.trim().split("\\s+"));

      String date = document.getString("date") != null ? document.getString("date") : "";
      String startTime =
          document.getString("startTime") != null ? document.getString("startTime") : "";
      String endTime = document.getString("endTime") != null ? document.getString("endTime") : "";

      Long eventIDLong = document.getLong("ID");
      int eventID = eventIDLong != null ? eventIDLong.intValue() : -1;

      String eventOrganizer =
          document.getString("eventOrganizer") != null ? document.getString("eventOrganizer") : "";

      events.add(
          new Event(
              listOfNames,
              listOfDescriptions,
              date,
              startTime,
              endTime,
              tags,
              eventID,
              eventOrganizer));
    }
    return events;
  }

  /**
   * Returns all events as a list of maps
   *
   * @return a list of Map<String, Object> of all the events
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public List<Map<String, Object>> getAllEventsMap()
      throws ExecutionException, InterruptedException {
    CollectionReference usersCollection = db.collection("users");

    List<Map<String, Object>> allEventData = new ArrayList<>();

    for (DocumentReference userDocRef : usersCollection.listDocuments()) {
      CollectionReference eventsCollection = userDocRef.collection("events");

      for (DocumentReference eventRef : eventsCollection.listDocuments()) {
        DocumentSnapshot eventSnapshot = eventRef.get().get();

        if (eventSnapshot.exists()) {
          Map<String, Object> eventData = eventSnapshot.getData();
          if (eventData != null) {
            allEventData.add(eventData);
          }
        }
      }
    }

    return allEventData;
  }

  /**
   * Updates whether a user is attending an event
   *
   * @param uid - clerkID of the user
   * @param eventID - unique ID of the event
   * @param isAttending - boolean that is true if the user is RSVPing (false if unRSVPing)
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoProfileFoundException - if profile not found
   * @throws NoEventFoundException - if event not found
   * @throws EventAlreadyAttendingException - if the user wants to RSVP but is already attending the
   *     event
   */
  public void updateAttending(String uid, int eventID, boolean isAttending)
      throws ExecutionException,
          InterruptedException,
          NoProfileFoundException,
          NoEventFoundException,
          EventAlreadyAttendingException {

    DocumentReference profileRef = db.collection("users").document(uid);
    DocumentReference eventRef = db.collection("events").document("event-" + eventID);

    DocumentSnapshot profileSnapshot = profileRef.get().get();
    DocumentSnapshot eventSnapshot = eventRef.get().get();

    if (!profileSnapshot.exists()) {
      throw new NoProfileFoundException("Profile does not exist.");
    }

    if (!eventSnapshot.exists()) {
      throw new NoEventFoundException("Event does not exist.");
    }

    // get and copy mutable list of eventsAttending
    List<Integer> oldAttending = (List<Integer>) profileSnapshot.get("eventsAttending");
    if (oldAttending == null) {
      oldAttending = new ArrayList<>();
    } else {
      oldAttending = new ArrayList<>(oldAttending); // make mutable copy
    }

    String eventCreatorUID = Objects.requireNonNull(eventSnapshot.get("uid")).toString();

    @SuppressWarnings("unchecked")
    List<String> oldEventAttendingList = (List<String>) eventSnapshot.get("usersAttending");
    if (oldEventAttendingList == null) {
      oldEventAttendingList = new ArrayList<>();
    }
    List<String> oldEventModifiable = new ArrayList<>(oldEventAttendingList);

    // Determine whether to add/remove attendance
    if (isAttending) {
      if (oldEventAttendingList.contains(uid)) {
        throw new EventAlreadyAttendingException("User already attending event.");
      }
      oldEventModifiable.add(uid);
      if (!oldAttending.contains(eventID)) {
        oldAttending.add(eventID);
      }
    } else {
      oldEventModifiable.remove(uid);
      oldAttending.remove(Integer.valueOf(eventID)); // remove by value, not index
    }

    // Update both the global event and user-specific event doc
    eventRef.update("usersAttending", oldEventModifiable);
    DocumentReference eventProfileRef =
        db.collection("users")
            .document(eventCreatorUID)
            .collection("events")
            .document("event-" + eventID);
    eventProfileRef.update("usersAttending", oldEventModifiable);

    // Update user's profile with new eventsAttending
    profileRef.update("eventsAttending", oldAttending);
  }

  /**
   * Checks if two profiles exist
   *
   * @param user1 - one of the users
   * @param user2 - the other user (can be null, then method only checks if user1 exists)
   * @throws NoProfileFoundException
   */
  private void checkProfilesExist(String user1, String user2) throws NoProfileFoundException {
    boolean foundUser1 = false;
    boolean foundUser2 = false;
    for (DocumentReference userRef : db.collection("users").listDocuments()) {
      if (userRef.getId().equals(user1)) {
        foundUser1 = true;
      } else if (userRef.getId().equals(user2)) {
        foundUser2 = true;
      }
    }

    if (!foundUser1 || (user2 != null && !foundUser2)) {
      throw new NoProfileFoundException("Profile does not exist.");
    }
  }

  /**
   * Adds a profile to the database
   *
   * @param uid - clerkID of the user
   * @param data - profile details of the user
   */
  public void addProfile(String uid, Map<String, Object> data) {
    db.collection("users").document(uid).set(data);
  }

  /**
   * Adds an event to a user's event history
   *
   * @param uid - clerkID of the user
   * @param eventID - ID for the event
   * @throws NoProfileFoundException - if profile not found
   * @throws NoEventFoundException - if event not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public void addEventHistory(String uid, String eventID)
      throws NoProfileFoundException,
          NoEventFoundException,
          ExecutionException,
          InterruptedException {
    DocumentReference userRef = db.collection("users").document(uid);
    if (!userRef.get().get().exists()) {
      throw new NoProfileFoundException("Profile not found.");
    }
    DocumentReference eventRef = db.collection("events").document("event-" + eventID);
    if (!eventRef.get().get().exists()) {
      throw new NoEventFoundException("Event not found.");
    }
    userRef.update("eventHistory", FieldValue.arrayUnion(getEvent(eventID)));
  }

  /**
   * Retrieves the events in a user's history
   *
   * @param uid - clerkID of the user
   * @return a list of Map<String,Object> that represents the events in a user's history
   * @throws NoProfileFoundException - if profile not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public List<Map<String, Object>> getEventHistory(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    DocumentReference userRef = db.collection("users").document(uid);
    return (List<Map<String, Object>>) userRef.get().get().get("eventHistory");
  }

  /**
   * Removes an event from a user's history
   *
   * @param uid - clerkID of the user
   * @param eventID - ID of the event
   * @throws NoProfileFoundException - if profile not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoEventFoundException - if event not found
   */
  public void removeEventHistory(String uid, String eventID)
      throws NoProfileFoundException,
          ExecutionException,
          InterruptedException,
          NoEventFoundException {
    this.checkProfilesExist(uid, null);
    DocumentReference userRef = db.collection("users").document(uid);
    List<Map<String, Object>> events =
        (List<Map<String, Object>>) userRef.get().get().get("eventHistory");
    for (Map<String, Object> event : events) {
      if (event.get("ID").toString().equals(eventID)) {
        userRef.update("eventHistory", FieldValue.arrayRemove(event));
        return;
      }
    }
    throw new NoEventFoundException("Event not found.");
  }

  /**
   * gets an event
   *
   * @param eventID - the ID for the event
   * @return the event as a Map<String, Object>
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoEventFoundException - if event not found
   */
  public Map<String, Object> getEvent(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException {
    DocumentReference docRef = db.collection("events").document("event-" + eventID);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot snapshot = future.get();

    if (!snapshot.exists()) {
      throw new NoEventFoundException("Event does not exist.");
    }

    Map<String, Object> data = snapshot.getData();

    // Ensure data isn't null
    if (data == null) {
      data = new HashMap<>();
    }

    // Explicitly add the UID if it's in the snapshot
    String uid = snapshot.getString("uid");
    if (uid != null) {
      data.put("uid", uid);
    }

    return data;
  }
}
