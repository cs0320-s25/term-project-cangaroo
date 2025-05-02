package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoExistingFriendRequestException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Exceptions.NotFriendsException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {

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
  }

  @Override
  public List<Map<String, Object>> getCollection(String user, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (user == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: user and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'user'

    Firestore db = FirestoreClient.getFirestore();
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

  @Override
  public Map<String, Object> getProfile(String uid)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("users").document(uid);

    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot snapshot = future.get();

    if (snapshot.exists()) {
      return snapshot.getData();
    } else {
      throw new NoProfileFoundException("Profile does not exist");
    }
  }

  @Override
  public List<Map<String, Object>> getCompleteCollection()
      throws InterruptedException, ExecutionException, IllegalArgumentException {

    Firestore db = FirestoreClient.getFirestore();

    // Get all pin documents for all users
    List<Map<String, Object>> data = new ArrayList<>();
    List<QueryDocumentSnapshot> docs = db.collectionGroup("pins").get().get().getDocuments();
    for (QueryDocumentSnapshot doc : docs) {
      data.add(doc.getData());
    }

    return data;
  }

  @Override
  public void updateEventID(int newVal) {
    Firestore db = FirestoreClient.getFirestore();
    Map<String, Object> data = new HashMap<>();
    data.put("eventID", newVal);
    db.collection("currentIDs").document("currentEventID").set(data);
  }

  @Override
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

  @Override
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

  @Override
  public void editProfile(String uid, List<String> tags, List<String> favEventOrganizers)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef =
        db.collection("users").document(uid).collection("profile").document("profileProperties");


    if (docRef.get().get().exists()) {
      DocumentSnapshot snapshot = docRef.get().get();

      if (tags != null) {
        List<String> existingTags = (List<String>) snapshot.get("interestedTags");
        if (existingTags == null) existingTags = new ArrayList<>();
        List<String> cleanedTags = tags.stream()
            .map(String::trim)
            .filter(t -> !t.isEmpty())
            .toList();
        Set<String> mergedTags = new HashSet<>(existingTags);
        mergedTags.addAll(cleanedTags);
        docRef.update("interestedTags", new ArrayList<>(mergedTags));
      }

      if (favEventOrganizers != null) {
        List<String> existingOrgs = (List<String>) snapshot.get("favoriteOrganizers");
        if (existingOrgs == null) existingOrgs = new ArrayList<>();
        List<String> cleanedOrgs = favEventOrganizers.stream()
            .map(String::trim)
            .filter(o -> !o.isEmpty())
            .toList();
        Set<String> mergedOrgs = new HashSet<>(existingOrgs);
        mergedOrgs.addAll(cleanedOrgs);
        docRef.update("favoriteOrganizers", new ArrayList<>(mergedOrgs));
      }

    } else {
      throw new NoProfileFoundException("No such profile.");
    }
  }

  @Override
  public List<Event> getAllEvents() throws ExecutionException, InterruptedException {

    Firestore db = FirestoreClient.getFirestore();
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

  @Override
  public void updateAttending(String uid, String eventID, boolean isAttending)
      throws ExecutionException,
          InterruptedException,
          NoProfileFoundException,
          NoEventFoundException {
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
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> oldAttending =
            (List<Map<String, Object>>) profileSnapshot.get("eventsAttending");
        assert oldAttending != null;
        String eventCreatorUID = Objects.requireNonNull(eventSnapshot.get("uid")).toString();
        @SuppressWarnings("unchecked")
        List<String> oldEventAttendingList = (List<String>) eventSnapshot.get("usersAttending");

        assert oldEventAttendingList != null;
        List<String> oldEventModifiable = new ArrayList<>(oldEventAttendingList);
        if (isAttending) {
          oldEventModifiable.add(uid);
        } else {
          oldEventModifiable.remove(uid);
        }
        eventRef.update("usersAttending", oldEventModifiable);
        DocumentReference eventProfileRef =
            db.collection("users")
                .document(eventCreatorUID)
                .collection("events")
                .document("event-" + eventID);
        eventProfileRef.update("usersAttending", oldEventModifiable);
        // update profile

        Map<String, Object> event = eventSnapshot.getData();
        assert event != null;

        if (isAttending) {
          event.put("usersAttending", oldEventModifiable);
          oldAttending.add(event);
        } else {
          oldAttending.remove(event);
        }

        System.out.println(event);
        profileRef.update("eventsAttending", oldAttending);
        // update event (get uid)

      } else {
        throw new NoEventFoundException("Event does not exist.");
      }
    } else {
      throw new NoProfileFoundException("Profile does not exist.");
    }
  }

  private void checkProfilesExist(String user1, String user2) throws NoProfileFoundException {
    // probably do some error-checking to see if the ids actually correspond to profiles
    Firestore db = FirestoreClient.getFirestore();
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

  @Override
  public void sendFriendRequest(String senderID, String receiverID) throws NoProfileFoundException {

    Firestore db = FirestoreClient.getFirestore();
    this.checkProfilesExist(senderID, receiverID);
    // create a document in the receiver's collection for the friend request
    db.collection("users")
        .document(senderID)
        .collection("outgoingFriendRequests")
        .document(receiverID)
        .set(new HashMap<>());
    // create document in 'outgoing friend requests' collection of sender
    db.collection("users")
        .document(receiverID)
        .collection("receivedFriendRequests")
        .document(senderID)
        .set(new HashMap<>());
  }

  @Override
  public void unsendFriendRequest(String senderID, String receiverID)
      throws NoProfileFoundException {
    Firestore db = FirestoreClient.getFirestore();
    // will throw NoProfileFoundException if profiles don't exist
    this.checkProfilesExist(senderID, receiverID);

    deleteDocument(
        db.collection("users")
            .document(senderID)
            .collection("outgoingFriendRequests")
            .document(receiverID));
    deleteDocument(
        db.collection("users")
            .document(receiverID)
            .collection("receivedFriendRequests")
            .document(senderID));
  }

  @Override
  public void respondToFriendRequest(String senderID, String receiverID, boolean isAccepted)
      throws NoProfileFoundException,
          ExecutionException,
          InterruptedException,
          NoExistingFriendRequestException {
    this.checkProfilesExist(senderID, receiverID);
    Firestore db = FirestoreClient.getFirestore();

    // check if there is a friend request there
    if (!db.collection("users")
        .document(senderID)
        .collection("outgoingFriendRequests")
        .document(receiverID)
        .get()
        .get()
        .exists()) {
      throw new NoExistingFriendRequestException("Friend request does not exist between friends.");
    }
    if (isAccepted) {
      // add to both friend lists
      db.collection("users")
          .document(senderID)
          .update("friendsList", FieldValue.arrayUnion(receiverID));

      db.collection("users")
          .document(receiverID)
          .update("friendsList", FieldValue.arrayUnion(senderID));
    }
    // remove from both friend request lists of each user after
    this.unsendFriendRequest(senderID, receiverID);
  }

  @Override
  public void addProfile(String uid, Map<String, Object> data) {
    Firestore db = FirestoreClient.getFirestore();
    //    db.collection("users")
    //        .document(uid)
    //        .collection("profile")
    //        .document("profileProperties")
    //        .set(data);
    db.collection("users").document(uid).set(data);
  }

  @Override
  public void deleteDatabase() {
    Firestore db = FirestoreClient.getFirestore();
    clearCollection(db.collection("users"));
    clearCollection(db.collection("events"));
    clearCollection(db.collection("currentIDs"));
  }

  @Override
  public void removeFriends(String user1, String user2)
      throws NoProfileFoundException,
          NotFriendsException,
          ExecutionException,
          InterruptedException {
    this.checkProfilesExist(user1, user2);

    Firestore db = FirestoreClient.getFirestore();

    // check if they are friends
    DocumentReference user1Ref = db.collection("users").document(user1);
    DocumentReference user2Ref = db.collection("users").document(user2);
    if (!((List<String>) (Objects.requireNonNull(user1Ref.get().get().get("friendsList"))))
        .contains(user2)) {
      throw new NotFriendsException("Not friends of user.");
    }

    user1Ref.update("friendsList", FieldValue.arrayRemove(user2));
    user2Ref.update("friendsList", FieldValue.arrayRemove(user1));
  }

  private String getNameFromID(String id) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    return db.collection("users").document(id).get().get().get("username").toString();
  }

  @Override
  public Map<String, String> viewFriends(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);
    Map<String, String> idToName = new HashMap<>();
    for (String friend : (List<String>) userRef.get().get().get("friendsList")) {
      idToName.put(friend, getNameFromID(friend));
    }
    return idToName;
  }

  @Override
  public Map<String, String> getFriendRequests(String uid, boolean isOutgoing)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    Firestore db = FirestoreClient.getFirestore();
    Map<String, String> idToName = new HashMap<>();
    String friendRequestCollection;
    if (isOutgoing) {
      friendRequestCollection = "outgoingFriendRequests";
    } else {
      friendRequestCollection = "receivedFriendRequests";
    }
    for (DocumentReference docRef :
        db.collection("users").document(uid).collection(friendRequestCollection).listDocuments()) {
      idToName.put(docRef.getId(), this.getNameFromID(docRef.getId()));
    }
    return idToName;
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
  public void addDocument(
      String user, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (user == null || collection_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: user, collection_id, doc_id, or data cannot be null");
    }
    // adds a new document 'doc_name' to collection 'collection_id' for user 'user'
    // with data payload 'data'.

    // TODO: FIRESTORE PART 1:
    // use the gusere below to implement this handler
    // - https://firebase.google.com/docs/firestore/quickstart#add_data

    Firestore db = FirestoreClient.getFirestore();
    CollectionReference collection =
        db.collection("users").document(user).collection(collection_id);
    collection.document(doc_id).set(data);
  }

  // clears the collections inside of a specific user.
  @Override
  public void clearUser(String user) throws IllegalArgumentException {
    if (user == null) {
      throw new IllegalArgumentException("removeUser: user cannot be null");
    }
    try {
      // removes all data for user 'user'
      Firestore db = FirestoreClient.getFirestore();
      // 1: Get a ref to the user document
      DocumentReference userDoc = db.collection("users").document(user);
      // 2: Delete the user document
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + user);
      System.err.println(e.getMessage());
    }
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

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
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

  // helper function to remove a collection by recursively removing its sub-documents
  private void clearCollection(CollectionReference collection) {
    for (DocumentReference docRef : collection.listDocuments()) {
      for (CollectionReference collectionRef : docRef.listCollections()) {
        deleteCollection(collectionRef);
      }
      deleteDocument(docRef);
    }
  }
}
