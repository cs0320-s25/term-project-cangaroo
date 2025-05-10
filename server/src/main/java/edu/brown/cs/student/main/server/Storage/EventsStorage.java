package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.student.main.server.Exceptions.EventAlreadyAttendingException;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EventsStorage extends FirebaseUtilitiesNew {

  public EventsStorage() throws IOException {
    super();
  }

  public void updateEventID(int newVal) {
    Firestore db = FirestoreClient.getFirestore();
    Map<String, Object> data = new HashMap<>();
    data.put("eventID", newVal);
    db.collection("currentIDs").document("currentEventID").set(data);
  }

  public int getCurrEventID() throws ExecutionException, InterruptedException {

    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("currentIDs").document("currentEventID");

    ApiFuture<DocumentSnapshot> future = docRef.get();
    // ...
    // future.get() blocks on response
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      return Integer.parseInt(Objects.requireNonNull(document.get("eventID")).toString());
    } else {
      return 0;
    }
  }

  public void editEvent(
      String uid,
      String eventID,
      String name,
      String description,
      String date,
      String startTime,
      String endTime,
      List<String> tags,
      String eventOrganizer)
      throws ExecutionException, InterruptedException, NoEventFoundException {
    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef =
        db.collection("users").document(uid).collection("events").document("event-" + eventID);

    DocumentReference eventRef = db.collection("events").document("event-" + eventID);
    if ((docRef.get().get().exists()) && (eventRef.get().get().exists())) {
      updateEventData(name, description, date, startTime, endTime, tags, eventOrganizer, docRef);
      updateEventData(name, description, date, startTime, endTime, tags, eventOrganizer, eventRef);
    } else {
      throw new NoEventFoundException("Event does not exist.");
    }
  }

  private void updateEventData(
      String name,
      String description,
      String date,
      String startTime,
      String endTime,
      List<String> tags,
      String eventOrganizer,
      DocumentReference docRef) {
    docRef.update("name", name);
    docRef.update("description", description);
    docRef.update("date", date);
    docRef.update("startTime", startTime);
    docRef.update("endTime", endTime);
    docRef.update("tags", tags);
    docRef.update("eventOrganizer", eventOrganizer);
  }

  public void updateAttending(String uid, int eventID, boolean isAttending)
      throws ExecutionException,
          InterruptedException,
          NoProfileFoundException,
          NoEventFoundException,
          EventAlreadyAttendingException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference profileRef = db.collection("users").document(uid);
    DocumentReference eventRef = db.collection("events").document("event-" + eventID);
    // change database architecture to allow storing all events in their own collection

    ApiFuture<DocumentSnapshot> profileFuture = profileRef.get();
    DocumentSnapshot profileSnapshot = profileFuture.get();

    ApiFuture<DocumentSnapshot> eventFuture = eventRef.get();
    DocumentSnapshot eventSnapshot = eventFuture.get();

    if (profileSnapshot.exists()) {
      if (eventSnapshot.exists()) {
        List<Integer> oldAttending = (List<Integer>) profileSnapshot.get("eventsAttending");
        assert oldAttending != null;
        assert oldAttending != null;
        String eventCreatorUID = Objects.requireNonNull(eventSnapshot.get("uid")).toString();
        @SuppressWarnings("unchecked")
        List<String> oldEventAttendingList = (List<String>) eventSnapshot.get("usersAttending");

        assert oldEventAttendingList != null;
        List<String> oldEventModifiable = new ArrayList<>(oldEventAttendingList);
        if (isAttending && !oldEventAttendingList.contains((long) eventID)) {
          oldEventModifiable.add(uid);
        } else if (!isAttending) {
          oldEventModifiable.remove(uid);
        } else {
          throw new EventAlreadyAttendingException("Event already attending");
        }
        eventRef.update("usersAttending", oldEventModifiable);
        DocumentReference eventProfileRef =
            db.collection("users")
                .document(eventCreatorUID)
                .collection("events")
                .document("event-" + eventID);
        eventProfileRef.update("usersAttending", oldEventModifiable);
        // update profile

        //        assert event != null;

        if (isAttending) {
          //          event.put("usersAttending", oldEventModifiable);
          oldAttending.add(eventID);
        } else {
          oldAttending.remove(eventID);
        }

        //        System.out.println(event);
        profileRef.update("eventsAttending", oldAttending);
        // update event (get uid)

      } else {
        throw new NoEventFoundException("Event does not exist.");
      }
    } else {
      throw new NoProfileFoundException("Profile does not exist.");
    }
  }

  public void addEventHistory(String uid, String eventID)
      throws NoProfileFoundException,
          NoEventFoundException,
          ExecutionException,
          InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);
    if (!userRef.get().get().exists()) {
      throw new NoProfileFoundException("Profile not found.");
    }
    DocumentReference eventRef = db.collection("events").document("event-" + eventID);
    if (!eventRef.get().get().exists()) {
      throw new NoEventFoundException("Event not found.");
    }

    userRef.update("eventHistory", FieldValue.arrayUnion(eventID));
  }

  public Map<String, Object> getEvent(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("events").document("event-" + eventID);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot snapshot = future.get();

    if (snapshot.exists()) {
      return snapshot.getData();
    } else {
      throw new NoEventFoundException("Event does not exist.");
    }
  }

  public void deleteEvent(String uid, String id) throws NoEventFoundException {

    Firestore db = FirestoreClient.getFirestore();
    CollectionReference dataRef = db.collection("users").document(uid).collection("events");

    for (DocumentReference eventRef : dataRef.listDocuments()) {
      if (eventRef.getId().equals("event-" + id)) {
        deleteDocument(eventRef);
        deleteDocument(db.collection("events").document("event-" + id));
        return;
      }
    }
    throw new NoEventFoundException("Event does not exist.");
  }

  public void addEvent(String user, int id, Map<String, Object> data)
      throws IllegalArgumentException,
          ExecutionException,
          InterruptedException,
          NoProfileFoundException {
    Firestore db = FirestoreClient.getFirestore();
    if (!db.collection("users").document(user).get().get().exists()) {
      throw new NoProfileFoundException("Profile does not exist");
    }
    CollectionReference collection = db.collection("users").document(user).collection("events");

    collection.document("event-" + id).set(data);
    db.collection("events").document("event-" + id).set(data);
  }
}
