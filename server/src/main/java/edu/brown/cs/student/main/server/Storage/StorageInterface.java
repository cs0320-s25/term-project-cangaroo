package edu.brown.cs.student.main.server.Storage;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.Exceptions.EventAlreadyAttendingException;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoExistingFriendRequestException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Exceptions.NotFriendsException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String user, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String user, String collection_id)
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getCompleteCollection() throws InterruptedException, ExecutionException;

  void clearUser(String user) throws InterruptedException, ExecutionException;

  void deleteEvent(String uid, String id) throws NoEventFoundException;

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
      String eventOrganizer,
      String thumbnailUrl)
      throws ExecutionException, InterruptedException, NoEventFoundException;

  void editProfile(
      String uid, List<String> tags, List<String> favEventOrganizers, String profilePicUrl)
      throws ExecutionException, InterruptedException, NoProfileFoundException;

  Map<String, Object> getProfile(String uid)
      throws ExecutionException, InterruptedException, NoProfileFoundException;

  List<Event> getAllEvents() throws ExecutionException, InterruptedException;

  List<Map<String, Object>> getAllEventsMap() throws ExecutionException, InterruptedException;

  void updateAttending(String uid, int eventID, boolean isAttending)
      throws ExecutionException,
          InterruptedException,
          NoProfileFoundException,
          NoEventFoundException,
          EventAlreadyAttendingException;

  void addEvent(String user, int id, Map<String, Object> data)
      throws IllegalArgumentException,
          ExecutionException,
          InterruptedException,
          NoProfileFoundException;

  void sendFriendRequest(String senderID, String receiverID) throws NoProfileFoundException;

  void unsendFriendRequest(String senderID, String receiverID) throws NoProfileFoundException;

  void respondToFriendRequest(String senderID, String receiverID, boolean isAccepted)
      throws NoProfileFoundException,
          ExecutionException,
          InterruptedException,
          NoExistingFriendRequestException;

  void addProfile(String uid, Map<String, Object> data);

  void deleteDatabase();

  void removeFriends(String user1, String user2)
      throws NoProfileFoundException, NotFriendsException, ExecutionException, InterruptedException;

  Map<String, String> viewFriends(String uid)
      throws NoProfileFoundException, ExecutionException, InterruptedException;

  Map<String, String> getFriendRequests(String uid, boolean isOutgoing)
      throws NoProfileFoundException, ExecutionException, InterruptedException;

  void addEventHistory(String uid, String eventID)
      throws NoProfileFoundException,
          NoEventFoundException,
          ExecutionException,
          InterruptedException;
}
