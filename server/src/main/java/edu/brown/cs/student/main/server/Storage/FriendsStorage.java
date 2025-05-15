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

/**
 * Class that manipulates friends data in the Firestore database
 */
public class FriendsStorage {
  // database object
  private Firestore db;

  /**
   * Manipulates friends data in the Firestore database
   * @param db - the Firestore database created by GeneralStorage
   */
  public FriendsStorage(Firestore db) throws IOException {
    this.db = db;
  }

  /**
   * Sends a friend request from one user to another
   * @param senderID - clerkID of the sender
   * @param receiverID - clerkID of the receiver
   * @throws NoProfileFoundException - if one of the profiles not found
   */
  public void sendFriendRequest(String senderID, String receiverID) throws NoProfileFoundException {
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

  /**
   * Unsends a friend request from one user to another
   * @param senderID - clerkID of the sender
   * @param receiverID - clerkID of the receiver
   * @throws NoProfileFoundException - if one of the profiles not found
   */
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

  /**
   * Responds a friend request from one user to another
   * @param senderID - clerkID of the sender
   * @param receiverID - clerkID of the receiver
   * @throws NoProfileFoundException - if one of the profiles not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   * @throws NoExistingFriendRequestException - if there is no friend request between them
   */
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

  /**
   * Unfriends two users
   * @param user1 - one of the users (order doesn't matter)
   * @param user2 - the other user (order doesn't matter)
   * @throws NoProfileFoundException - if profile not found
   * @throws NotFriendsException - if users weren't friends in the first place
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public void removeFriends(String user1, String user2)
      throws NoProfileFoundException,
          NotFriendsException,
          ExecutionException,
          InterruptedException {
    this.checkProfilesExist(user1, user2);

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

  /**
   * Gets the friends of a user
   * @param uid - clerkID of the user
   * @return a HashMap that maps uid -> username
   * @throws NoProfileFoundException - if no profile found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public Map<String, String> viewFriends(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
    DocumentReference userRef = db.collection("users").document(uid);
    Map<String, String> idToName = new HashMap<>();
    for (String friend : (List<String>) userRef.get().get().get("friendsList")) {
      idToName.put(friend, getNameFromID(friend));
    }
    return idToName;
  }

  /**
   * Gets either all outgoing or received friend requests
   * @param uid - clerkID of the user
   * @param isOutgoing - a boolean that is true when intending to get all outgoing requests
   * @return a Hashmap from uid -> username
   * @throws NoProfileFoundException - if profile not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public Map<String, String> getFriendRequests(String uid, boolean isOutgoing)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
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

  /**
   * Gets all non-friends of a user
   * @param uid - clerkID of a user
   * @return - a map from uid -> username
   * @throws NoProfileFoundException - if profile not found
   * @throws ExecutionException - if the computation threw an exception
   * @throws InterruptedException - if the current thread was interrupted while waiting
   */
  public Map<String, String> getUsers(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException {
    this.checkProfilesExist(uid, null);
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

  /**
   *
   * @param id
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  private String getNameFromID(String id) throws ExecutionException, InterruptedException {
    //    Firestore db = FirestoreClient.getFirestore();
    return db.collection("users").document(id).get().get().get("username").toString();
  }

  /**
   * Checks if two profiles exist
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
   * Helper method to delete a document
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
