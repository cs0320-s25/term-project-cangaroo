package edu.brown.cs.student.main.server.Storage;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ProfileStorage extends FirebaseUtilitiesNew {

  public ProfileStorage() throws IOException {
    super();
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

  public void editProfile(String uid, List<String> tags, List<String> favEventOrganizers)
      throws ExecutionException, InterruptedException, NoProfileFoundException {

    //    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef = db.collection("users").document(uid);

    if (docRef.get().get().exists()) {
      DocumentSnapshot snapshot = docRef.get().get();

      if (tags != null) {
        List<String> existingTags = (List<String>) snapshot.get("interestedTags");
        if (existingTags == null) existingTags = new ArrayList<>();
        List<String> cleanedTags =
            tags.stream().map(String::trim).filter(t -> !t.isEmpty()).toList();
        Set<String> mergedTags = new HashSet<>(existingTags);
        mergedTags.addAll(cleanedTags);
        docRef.update("interestedTags", new ArrayList<>(mergedTags));
      }

      if (favEventOrganizers != null) {
        List<String> existingOrgs = (List<String>) snapshot.get("interestedOrganizations");
        if (existingOrgs == null) existingOrgs = new ArrayList<>();
        List<String> cleanedOrgs =
            favEventOrganizers.stream().map(String::trim).filter(o -> !o.isEmpty()).toList();
        Set<String> mergedOrgs = new HashSet<>(existingOrgs);
        mergedOrgs.addAll(cleanedOrgs);
        docRef.update("interestedOrganizations", new ArrayList<>(mergedOrgs));
      }

    } else {
      throw new NoProfileFoundException("No such profile.");
    }
  }

  public void addProfile(String uid, Map<String, Object> data) {
    //    Firestore db = FirestoreClient.getFirestore();
    db.collection("users").document(uid).set(data);
  }
}
