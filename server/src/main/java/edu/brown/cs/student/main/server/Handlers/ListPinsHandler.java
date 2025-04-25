// package edu.brown.cs.student.main.server.Handlers;
//
// import edu.brown.cs.student.main.server.Storage.StorageInterface;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import spark.Request;
// import spark.Response;
// import spark.Route;
//
// public class ListPinsHandler implements Route {
//
//  public StorageInterface storageHandler;
//
//  public ListPinsHandler(StorageInterface storageHandler) {
//    this.storageHandler = storageHandler;
//  }
//
//  /**
//   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
//   *
//   * @param request The request object providing information about the HTTP request
//   * @param response The response object providing functionality for modifying the response
//   * @return The content to be set in the response
//   */
//  @Override
//  public Object handle(Request request, Response response) {
//    Map<String, Object> responseMap = new HashMap<>();
//    try {
//      final String user = request.queryParams("user");
//      responseMap.put("user", user);
//      if (user == null) {
//        responseMap.put("response_type", "failure");
//        responseMap.put("error", "Missing required parameter (user)");
//        return responseMap;
//      }
//
//      System.out.println("listing pins for user: " + user);
//
//      // get all the pins for the user
//      List<Map<String, Object>> vals = this.storageHandler.getCollection(user, "pins");
//
//      // convert the key,value map to just a list of the pins.
//      List<Pin> pins =
//          vals.stream()
//              .map(
//                  pin ->
//                      new Pin(
//                          user, pin.get("latitude").toString(), pin.get("longitude").toString()))
//              .toList();
//
//      responseMap.put("response_type", "success");
//      responseMap.put("pins", pins);
//    } catch (Exception e) {
//      // error likely occurred in the storage handler
//      e.printStackTrace();
//      responseMap.put("response_type", "failure");
//      responseMap.put("error", e.getMessage());
//    }
//
//    return Utils.toMoshiJson(responseMap);
//  }
// }
