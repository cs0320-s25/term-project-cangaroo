package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import edu.brown.cs.student.main.server.Exceptions.NoExistingFriendRequestException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Exceptions.NotFriendsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FriendsStorage {
  private Firestore db;

  public FriendsStorage(Firestore db) throws IOException {
    this.db = db;
  }

  public void sendFriendRequest(String senderID, String receiverID) throws NoProfileFoundException {

    //    Firestore db = FirestoreClient.getFirestore();
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

  public void unsendFriendRequest(String senderID, String receiverID)
      throws NoProfileFoundException {
    //    Firestore db = FirestoreClient.getFirestore();
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

  public void respondToFriendRequest(String senderID, String receiverID, boolean isAccepted)
      throws NoProfileFoundException,
          ExecutionException,
          InterruptedException,
          NoExistingFriendRequestException {
    this.checkProfilesExist(senderID, receiverID);
    //    Firestore db = FirestoreClient.getFirestore();

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

  public void removeFriends(String user1, String user2)
      throws NoProfileFoundException,
          NotFriendsException,
          ExecutionException,
          InterruptedException {
    this.checkProfilesExist(user1, user2);

    //    Firestore db = FirestoreClient.getFirestore();

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

  public Map<String, String> viewFriends(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    //    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);
    Map<String, String> idToName = new HashMap<>();
    for (String friend : (List<String>) userRef.get().get().get("friendsList")) {
      idToName.put(friend, getNameFromID(friend));
    }
    return idToName;
  }

  public Map<String, String> getFriendRequests(String uid, boolean isOutgoing)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    //    Firestore db = FirestoreClient.getFirestore();
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

  public Map<String, String> getUsers(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    //    Firestore db = FirestoreClient.getFirestore();
    DocumentReference userRef = db.collection("users").document(uid);
    List<String> friends = (List<String>) userRef.get().get().get("friendsList");

    Iterable<DocumentReference> friendRequests =
        userRef.collection("outgoingFriendRequests").listDocuments();
    List<String> friendRequestIDs = new ArrayList<>();
    for (DocumentReference friendRequest : friendRequests) {
      friendRequestIDs.add(friendRequest.getId());
    }

    assert friends != null;
    Map<String, String> usersList = new HashMap<>();
    for (DocumentReference ref : db.collection("users").listDocuments()) {
      System.out.println(ref.getId() + " " + getNameFromID(ref.getId()));
      if (!ref.getId().equals(uid)
          && !friends.contains(ref.getId())
          && !friendRequestIDs.contains(ref.getId())) {

        usersList.put(ref.getId(), getNameFromID(ref.getId()));
      }
    }
    return usersList;
  }

  private String getNameFromID(String id) throws ExecutionException, InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
    return db.collection("users").document(id).get().get().get("username").toString();
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
