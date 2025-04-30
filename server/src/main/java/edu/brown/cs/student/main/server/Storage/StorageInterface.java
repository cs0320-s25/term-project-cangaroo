package edu.brown.cs.student.main.server.Storage;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String user, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String user, String collection_id)
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getCompleteCollection() throws InterruptedException, ExecutionException;

  void clearUser(String user) throws InterruptedException, ExecutionException;

  void deleteEvent(String uid, String collection_id, String id)
      throws NoEventFoundException, ExecutionException, InterruptedException;

  void updateEventID(int newVal);

  int getCurrEventID() throws ExecutionException, InterruptedException;

  Map<String, Object> getEvent(String eventID)
      throws ExecutionException, InterruptedException, NoEventFoundException;

  void editEvent(
      String uid,
      String eventID,
      String name,
      String description,
      String date,
      String startTime,
      String endTime,
      List<String> tags,
      String eventOrganizer)
      throws ExecutionException, InterruptedException, NoEventFoundException;

  void editProfile(String uid, List<String> tags)
      throws ExecutionException, InterruptedException, NoProfileFoundException;

  Map<String, Object> getProfile(String uid)
      throws ExecutionException, InterruptedException, NoProfileFoundException;

  List<Event> getAllEvents() throws ExecutionException, InterruptedException;

  void updateAttending(String uid, String eventID, boolean isAttending)
      throws ExecutionException,
          InterruptedException,
          NoProfileFoundException,
          NoEventFoundException;

  void addEvent(String user, int id, Map<String, Object> data) throws IllegalArgumentException;
}
