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
import java.util.List;

/**
 * Class that manipulates miscellaneous data in the Firebase database; starts the Firebase
 * application
 */
public class GeneralStorage {
  // database
  public Firestore db;

  /**
   * Initializes the Firebase application and creates a database object
   *
   * @throws IOException - if error occurs while starting app
   */
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

  /** Deletes the entire database (used for testing) */
  public void deleteDatabase() {
    clearCollection(db.collection("users"));
    clearCollection(db.collection("events"));
    clearCollection(db.collection("currentIDs"));
  }

  /**
   * Clears a collection of all documents
   *
   * @param collection - the reference to the collection to be deleted
   */
  private void clearCollection(CollectionReference collection) {
    for (DocumentReference docRef : collection.listDocuments()) {
      for (CollectionReference collectionRef : docRef.listCollections()) {
        deleteCollection(collectionRef);
      }
      deleteDocument(docRef);
    }
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
}
