package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.Handlers.ChangeAttendanceHandler;
import edu.brown.cs.student.main.server.Handlers.DeleteEventHandler;
import edu.brown.cs.student.main.server.Handlers.EditEventHandler;
import edu.brown.cs.student.main.server.Handlers.EditProfileHandler;
import edu.brown.cs.student.main.server.Handlers.EventCreationHandler;
import edu.brown.cs.student.main.server.Handlers.GetFriendsHandler;
import edu.brown.cs.student.main.server.Handlers.ProfileCreationHandler;
import edu.brown.cs.student.main.server.Handlers.RSVPHandler;
import edu.brown.cs.student.main.server.Handlers.RandomRecommendHandler;
import edu.brown.cs.student.main.server.Handlers.RateHandler;
import edu.brown.cs.student.main.server.Handlers.RecommendHandler;
import edu.brown.cs.student.main.server.Handlers.RemoveFriendHandler;
import edu.brown.cs.student.main.server.Handlers.SearchHandler;
import edu.brown.cs.student.main.server.Handlers.SendFriendRequestHandler;
import edu.brown.cs.student.main.server.Handlers.UnsendFriendRequestHandler;
import edu.brown.cs.student.main.server.Handlers.ViewEventHandler;
import edu.brown.cs.student.main.server.Handlers.ViewProfileHandler;
import edu.brown.cs.student.main.server.Storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.Storage.StorageInterface;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {

    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    // pondered mocking this, but figured it would be too complex mock the entire database
    StorageInterface firebaseUtils;
    try {
      StorageInterface storageHandler = new FirebaseUtilities();

      Spark.get("send-friend-request", new SendFriendRequestHandler(storageHandler));
      Spark.get("unsend-friend-request", new UnsendFriendRequestHandler(storageHandler));
      Spark.get("remove-friend", new RemoveFriendHandler());
      Spark.get("edit-event", new EditEventHandler(storageHandler));
      Spark.get("edit-profile", new EditProfileHandler(storageHandler));
      Spark.get("create-event", new EventCreationHandler(storageHandler));
      Spark.get("get-friends", new GetFriendsHandler(storageHandler));
      Spark.get("random-recommend", new RandomRecommendHandler());
      Spark.get("rate", new RateHandler());
      Spark.get("recommend", new RecommendHandler());
      Spark.get("delete-event", new DeleteEventHandler(storageHandler));
      Spark.get("rsvp", new RSVPHandler());
      Spark.get("search", new SearchHandler());
      Spark.get("create-profile", new ProfileCreationHandler(storageHandler));
      Spark.get("view-event", new ViewEventHandler(storageHandler));
      Spark.get("view-profile", new ViewProfileHandler(storageHandler));
      Spark.get("change-attendance", new ChangeAttendanceHandler(storageHandler));

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}
