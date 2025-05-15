package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EventsStorage {
  private Firestore db;

  public EventsStorage(Firestore db) throws IOException {
    this.db = db;
  }

  public void updateEventID(int newVal) {
    //    Firestore db = FirestoreClient.getFirestore();
    Map<String, Object> data = new HashMap<>();
    data.put("eventID", newVal);
    db.collection("currentIDs").document("currentEventID").set(data);
  }

  public int getCurrEventID() throws ExecutionException, InterruptedException {

    //    Firestore db = FirestoreClient.getFirestore();
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
      String eventOrganizer,
      String thumbnailUrl)
      throws ExecutionException, InterruptedException, NoEventFoundException {
    //    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef =
        db.collection("users").document(uid).collection("events").document("event-" + eventID);

    DocumentReference eventRef = db.collection("events").document("event-" + eventID);
    if ((docRef.get().get().exists()) && (eventRef.get().get().exists())) {
      updateEventData(
          name, description, date, startTime, endTime, tags, eventOrganizer, thumbnailUrl, docRef);
      updateEventData(
          name,
          description,
          date,
          startTime,
          endTime,
          tags,
          eventOrganizer,
          thumbnailUrl,
          eventRef);
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
      String thumbnailUrl,
      DocumentReference docRef) {
    docRef.update("name", name);
    docRef.update("description", description);
    docRef.update("date", date);
    docRef.update("startTime", startTime);
    docRef.update("endTime", endTime);
    docRef.update("tags", tags);
    docRef.update("eventOrganizer", eventOrganizer);
    docRef.update("thumbnailUrl", thumbnailUrl);
  }

  private String getNameFromID(String id) throws ExecutionException, InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
    return db.collection("users").document(id).get().get().get("username").toString();
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

  public void deleteEvent(String uid, String id) throws NoEventFoundException {

    //    Firestore db = FirestoreClient.getFirestore();
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
    //    Firestore db = FirestoreClient.getFirestore();
    if (!db.collection("users").document(user).get().get().exists()) {
      throw new NoProfileFoundException("Profile does not exist");
    }
    CollectionReference collection = db.collection("users").document(user).collection("events");

    collection.document("event-" + id).set(data);
    db.collection("events").document("event-" + id).set(data);
  }

  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }
}
