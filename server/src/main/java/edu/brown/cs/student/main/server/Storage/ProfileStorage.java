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

public class ProfileStorage {
  private Firestore db;

  public ProfileStorage(Firestore db) throws IOException {
    this.db = db;
  }

  public Map<String, Object> getProfile(String uid)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    //    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("users").document(uid);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot snapshot = future.get();

    if (snapshot.exists()) {
      return snapshot.getData();
    } else {
      throw new NoProfileFoundException("Profile does not exist");
    }
  }

  public void editProfile(
      String uid, List<String> tags, List<String> favEventOrganizers, String profilePicUrl)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    //    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef = db.collection("users").document(uid);
    if (docRef.get().get().exists()) {
      docRef.update("interestedTags", tags);
      docRef.update("interestedOrganizations", favEventOrganizers);
      docRef.update("profilePicUrl", profilePicUrl);
    } else {
      throw new NoProfileFoundException("No such profile.");
    }
  }

  public List<Event> getAllEvents() throws ExecutionException, InterruptedException, IOException {

    //    Firestore db = FirestoreClient.getFirestore();
    //    CollectionReference collection = db.collection("users");
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

  public List<Map<String, Object>> getAllEventsMap()
      throws ExecutionException, InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
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

  private void checkProfilesExist(String user1, String user2) throws NoProfileFoundException {
    // probably do some error-checking to see if the ids actually correspond to profiles
    //    Firestore db = FirestoreClient.getFirestore();
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

  public void addProfile(String uid, Map<String, Object> data) {
    //    Firestore db = FirestoreClient.getFirestore();
    //    db.collection("users")
    //        .document(uid)
    //        .collection("profile")
    //        .document("profileProperties")
    //        .set(data);
    db.collection("users").document(uid).set(data);
  }

  public void addEventHistory(String uid, String eventID)
      throws NoProfileFoundException,
          NoEventFoundException,
          ExecutionException,
          InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
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

  public List<Map<String, Object>> getEventHistory(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    //    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);
    return (List<Map<String, Object>>) userRef.get().get().get("eventHistory");
  }

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

  public Map<String, Object> getEvent(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException {

    //    Firestore db = FirestoreClient.getFirestore();
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
