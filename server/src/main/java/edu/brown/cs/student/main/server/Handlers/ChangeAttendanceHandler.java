package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Exceptions.EventAlreadyAttendingException;
import edu.brown.cs.student.main.server.Exceptions.NoEventFoundException;
import edu.brown.cs.student.main.server.Exceptions.NoProfileFoundException;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ChangeAttendanceHandler implements Route {

  public StorageInterface storageHandler;

  public ChangeAttendanceHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String uid = request.queryParams("uid");
    String eventID = request.queryParams("eventID");
    String isAttending = request.queryParams("isAttending");

    if ((uid == null)
        || (eventID == null)
        || (!isAttending.equalsIgnoreCase("false") && !isAttending.equalsIgnoreCase("true"))) {
      responseMap.put("result", "success");
      responseMap.put(
          "error_message", "Missing required parameters: uid, eventID, isAttending (boolean)");
      return Utils.toMoshiJson(responseMap);
    }

    try {
      this.storageHandler.updateAttending(
          uid, Integer.parseInt(eventID), Boolean.parseBoolean(isAttending));
      responseMap.put("result", "success");
    } catch (NoProfileFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Profile does not exist.");
    } catch (NoEventFoundException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "Event does not exist.");
    } catch (EventAlreadyAttendingException e) {
      responseMap.put("result", "failure");
      responseMap.put("error_message", "User already attending event.");
    }
    return Utils.toMoshiJson(responseMap);
  }
}
