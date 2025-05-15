package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Storage.GeneralStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler that deletes the entire database (used for testing) */
public class DeleteAllHandler implements Route {
  // database object
  public GeneralStorage storageHandler;

  /**
   * Handler that deletes the entire database (used for testing)
   *
   * @param storageHandler - a GeneralStorage object that has the Firestore object
   */
  public DeleteAllHandler(GeneralStorage storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      this.storageHandler.deleteDatabase();
      responseMap.put("result", "success");
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
    }

    return Utils.toMoshiJson(responseMap);
  }
}
