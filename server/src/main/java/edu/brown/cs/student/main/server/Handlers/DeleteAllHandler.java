package edu.brown.cs.student.main.server.Handlers;

import edu.brown.cs.student.main.server.Storage.GeneralStorage;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteAllHandler implements Route {
  public GeneralStorage storageHandler;

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
