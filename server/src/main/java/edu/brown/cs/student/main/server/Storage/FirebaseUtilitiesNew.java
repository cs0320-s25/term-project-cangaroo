package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Profiles.Profile;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilitiesNew {
  public Firestore db;

  public FirebaseUtilitiesNew() throws IOException {

    // TODO: FIRESTORE PART 0:
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.
    System.out.println("Firebase Config Path: " + firebaseConfigPath);
    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);

    this.db = FirestoreClient.getFirestore();
  }

  //
  void deleteDocument(DocumentReference doc) {
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

  public List<Map<String, Object>> getCollection(String user, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (user == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: user and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'user'

    //    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(user).collection(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  public Profile getProfileRecord(DocumentSnapshot snapshot) {
    String username = snapshot.getString("username");

    List<String> interestedTags = (List<String>) snapshot.get("interestedTags");
    if (interestedTags == null) interestedTags = new ArrayList<>();

    List<String> friendNames = (List<String>) snapshot.get("friendsList");
    if (friendNames == null) friendNames = new ArrayList<>();

    List<Long> rawEvents = (List<Long>) snapshot.get("eventsAttending");
    List<Integer> eventsAttending = new ArrayList<>();
    if (rawEvents != null) {
      for (Long eventId : rawEvents) {
        eventsAttending.add(eventId.intValue());
      }
    }

    List<String> interestedOrganizations = (List<String>) snapshot.get("interestedOrganizations");
    if (interestedOrganizations == null) interestedOrganizations = new ArrayList<>();

    return new Profile(
        username, interestedTags, friendNames, eventsAttending, interestedOrganizations);
  }

  public List<Map<String, Object>> getCompleteCollection()
      throws InterruptedException, ExecutionException, IllegalArgumentException {

    //    Firestore db = FirestoreClient.getFirestore();

    // Get all pin documents for all users
    List<Map<String, Object>> data = new ArrayList<>();
    List<QueryDocumentSnapshot> docs = db.collectionGroup("pins").get().get().getDocuments();
    for (QueryDocumentSnapshot doc : docs) {
      data.add(doc.getData());
    }

    return data;
  }

  public List<Event> getAllEvents() throws ExecutionException, InterruptedException {

    //    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collection = db.collection("users");
    ArrayList<Event> events = new ArrayList<>();

    for (DocumentReference userDocRef : collection.listDocuments()) {
      for (DocumentReference eventRef : userDocRef.collection("events").listDocuments()) {

        ApiFuture<DocumentSnapshot> future = eventRef.get();
        DocumentSnapshot document = future.get();

        List<String> name = Arrays.asList(document.get("name").toString().split(" "));
        List<String> description = Arrays.asList(document.get("description").toString().split(" "));
        String date = document.get("date").toString();
        String startTime = document.get("startTime").toString();
        String endTime = document.get("endTime").toString();
        int eventID = Integer.parseInt(document.get("eventID").toString());
        String eventOrganizer = document.get("eventOrganizer").toString();

        List<String> tags = Arrays.asList(document.get("tags").toString().split(" "));

        events.add(
            new Event(name, description, date, startTime, endTime, tags, eventID, eventOrganizer));
      }
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

  void checkProfilesExist(String user1, String user2) throws NoProfileFoundException {
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

  public void deleteDatabase() {
    //    Firestore db = FirestoreClient.getFirestore();
    clearCollection(db.collection("users"));
    clearCollection(db.collection("events"));
    clearCollection(db.collection("currentIDs"));
  }

  String getNameFromID(String id) throws ExecutionException, InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
    return db.collection("users").document(id).get().get().get("username").toString();
  }

  public Event getEventRecord(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException {
    //    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("events").document("event-" + eventID);

    DocumentSnapshot snapshot = docRef.get().get();

    if (!snapshot.exists()) {
      throw new NoEventFoundException("Event does not exist.");
    }

    List<String> name = Arrays.asList(snapshot.get("name").toString().split(" "));
    List<String> description = Arrays.asList(snapshot.get("description").toString().split(" "));
    String date = snapshot.getString("date");
    String startTime = snapshot.getString("startTime");
    String endTime = snapshot.getString("endTime");
    int parsedEventID = Integer.parseInt(snapshot.get("eventID").toString());
    String eventOrganizer = snapshot.getString("eventOrganizer");
    List<String> tags = Arrays.asList(snapshot.get("tags").toString().split(" "));

    return new Event(
        name, description, date, startTime, endTime, tags, parsedEventID, eventOrganizer);
  }

  private void clearCollection(CollectionReference collection) {
    for (DocumentReference docRef : collection.listDocuments()) {
      for (CollectionReference collectionRef : docRef.listCollections()) {
        deleteCollection(collectionRef);
      }
      deleteDocument(docRef);
    }
  }
}
