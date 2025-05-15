package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GeneralStorage {
  public Firestore db;

  public GeneralStorage() throws IOException {

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

  public void deleteDatabase() {
    //    Firestore db = FirestoreClient.getFirestore();
    clearCollection(db.collection("users"));
    clearCollection(db.collection("events"));
    clearCollection(db.collection("currentIDs"));
  }

  private void clearCollection(CollectionReference collection) {
    for (DocumentReference docRef : collection.listDocuments()) {
      for (CollectionReference collectionRef : docRef.listCollections()) {
        deleteCollection(collectionRef);
      }
      deleteDocument(docRef);
    }
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

  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }
}
