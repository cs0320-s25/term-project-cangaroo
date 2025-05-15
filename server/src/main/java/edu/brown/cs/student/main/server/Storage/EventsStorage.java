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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/** Class that manipulates event data in the Firestore database */
public class EventsStorage {
  // database object
  private Firestore db;

  /**
   * Manipulates event data in the Firestore database
   *
   * @param db - the Firestore database created by GeneralStorage
   */
  public EventsStorage(Firestore db) {
    this.db = db;
  }

  /**
   * Gets the current available eventID
   *
   * @return the current available eventID
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public int getCurrEventID() throws ExecutionException, InterruptedException {

    DocumentReference docRef = db.collection("currentIDs").document("currentEventID");
    DocumentSnapshot document = docRef.get().get();
    if (document.exists()) {
      return Integer.parseInt(Objects.requireNonNull(document.get("eventID")).toString());
    } else {
      // return 0 as the first ID if not in the database
      return 0;
    }
  }

  /**
   * Edits the details of an event in the database
   *
   * @param uid - clerkID of the creator of the event
   * @param eventID - the unique ID for an event
   * @param name - name of the event
   * @param description - event's description
   * @param date - YYYY/MM/DD format
   * @param startTime - starting time in military time
   * @param endTime - starting time in military time
   * @param tags - a list of strings that represents the tags associated with the event
   * @param eventOrganizer - name of the event organizer
   * @param thumbnailUrl - url that links to the thumbnail
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoEventFoundException - if the event doesn't exist
   */
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

    // need to update the event in the list of events as well as the user's list of events
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

  /**
   * helper method that updates the event details for an event
   *
   * @param name - name of the event
   * @param description - event's description
   * @param date - YYYY/MM/DD format
   * @param startTime - starting time in military time
   * @param endTime - starting time in military time
   * @param tags - a list of strings that represents the tags associated with the event
   * @param eventOrganizer - name of the event organizer
   * @param thumbnailUrl - url that links to the thumbnail
   * @param docRef - DocumentReference of where the data is being updated
   */
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

  /**
   * returns an Event given its ID
   *
   * @param eventID - the unique eventID for the event
   * @return a Map<String, Object> that represents an Event
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws ExecutionException - if the computation threw an exception
   * @throws NoEventFoundException - if the event doesn't exist
   * @throws NoEventFoundException - if the event wasn't found
   */
  public Map<String, Object> getEvent(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException {

    DocumentReference docRef = db.collection("events").document("event-" + eventID);

    DocumentSnapshot snapshot = docRef.get().get();

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

  /**
   * Deletes an event in the database
   *
   * @param uid - clerkID of the creator of the event
   * @param id - eventID of the event
   * @throws NoEventFoundException - if the event isn't found
   */
  public void deleteEvent(String uid, String id) throws NoEventFoundException {

    CollectionReference dataRef = db.collection("users").document(uid).collection("events");

    for (DocumentReference eventRef : dataRef.listDocuments()) {
      if (eventRef.getId().equals("event-" + id)) {
        // delete the document in both the user's events and the big list of events
        deleteDocument(eventRef);
        deleteDocument(db.collection("events").document("event-" + id));
        return;
      }
    }
    throw new NoEventFoundException("Event does not exist.");
  }

  /**
   * Adds an event to the database
   *
   * @param user - clerkID of the creator
   * @param id - eventID of the event
   * @param data - the event details
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws ExecutionException - if the computation threw an exception
   * @throws NoProfileFoundException - if profile not found
   */
  public void addEvent(String user, int id, Map<String, Object> data)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    if (!db.collection("users").document(user).get().get().exists()) {
      throw new NoProfileFoundException("Profile does not exist");
    }

    CollectionReference collection = db.collection("users").document(user).collection("events");

    collection.document("event-" + id).set(data);
    db.collection("events").document("event-" + id).set(data);
  }

  /**
   * Updates the next available eventID
   *
   * @param newVal - the next available eventID
   */
  public void updateEventID(int newVal) {
    //    Firestore db = FirestoreClient.getFirestore();
    Map<String, Object> data = new HashMap<>();
    data.put("eventID", newVal);
    db.collection("currentIDs").document("currentEventID").set(data);
  }

  /**
   * Helper method to delete a document
   *
   * @param doc - the DocumentReference to the document
   */
  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  /**
   * Helper method to delete a collection
   *
   * @param collection - a reference to the collection
   */
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
