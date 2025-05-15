package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.Handlers.AddEventHistoryHandler;
import edu.brown.cs.student.main.server.Handlers.ChangeAttendanceHandler;
import edu.brown.cs.student.main.server.Handlers.DeleteAllHandler;
import edu.brown.cs.student.main.server.Handlers.DeleteEventHandler;
import edu.brown.cs.student.main.server.Handlers.EditEventHandler;
import edu.brown.cs.student.main.server.Handlers.EditProfileHandler;
import edu.brown.cs.student.main.server.Handlers.EventCreationHandler;
import edu.brown.cs.student.main.server.Handlers.GetEventHistoryHandler;
import edu.brown.cs.student.main.server.Handlers.GetNonFriendsHandler;
import edu.brown.cs.student.main.server.Handlers.GetOutgoingFriendRequestsHandler;
import edu.brown.cs.student.main.server.Handlers.GetReceivedFriendRequestsHandler;
import edu.brown.cs.student.main.server.Handlers.MostAttendedEventsFriendHandler;
import edu.brown.cs.student.main.server.Handlers.ProfileCreationHandler;
import edu.brown.cs.student.main.server.Handlers.RandomRecommendHandler;
import edu.brown.cs.student.main.server.Handlers.RecommendHandler;
import edu.brown.cs.student.main.server.Handlers.RemoveEventHistoryHandler;
import edu.brown.cs.student.main.server.Handlers.RespondToFriendRequestHandler;
import edu.brown.cs.student.main.server.Handlers.SearchHandler;
import edu.brown.cs.student.main.server.Handlers.SendFriendRequestHandler;
import edu.brown.cs.student.main.server.Handlers.UnfriendHandler;
import edu.brown.cs.student.main.server.Handlers.UnsendFriendRequestHandler;
import edu.brown.cs.student.main.server.Handlers.ViewEventHandler;
import edu.brown.cs.student.main.server.Handlers.ViewFriendsHandler;
import edu.brown.cs.student.main.server.Handlers.ViewProfileHandler;
import edu.brown.cs.student.main.server.Storage.EventsStorage;
import edu.brown.cs.student.main.server.Storage.FriendsStorage;
import edu.brown.cs.student.main.server.Storage.GeneralStorage;
import edu.brown.cs.student.main.server.Storage.ProfileStorage;
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

    GeneralStorage generalStorage;
    EventsStorage eventsStorage;
    ProfileStorage profileStorage;
    FriendsStorage friendsStorage;
    try {
      /*
      should instantiate the GeneralStorage first, since it starts the Firebase app and creates
      the Firestore object
       */

      generalStorage = new GeneralStorage();
      eventsStorage = new EventsStorage(generalStorage.db);
      profileStorage = new ProfileStorage(generalStorage.db);
      friendsStorage = new FriendsStorage(generalStorage.db);

      // set up endpoints
      Spark.get("send-friend-request", new SendFriendRequestHandler(friendsStorage));
      Spark.get("unsend-friend-request", new UnsendFriendRequestHandler(friendsStorage));
      Spark.get("edit-event", new EditEventHandler(eventsStorage));
      Spark.get("edit-profile", new EditProfileHandler(profileStorage));
      Spark.get("create-event", new EventCreationHandler(eventsStorage));
      Spark.get("random-recommend", new RandomRecommendHandler(profileStorage));
      Spark.get("recommend", new RecommendHandler(profileStorage));
      Spark.get("delete-event", new DeleteEventHandler(eventsStorage));
      Spark.get("search", new SearchHandler(profileStorage));
      Spark.get("create-profile", new ProfileCreationHandler(profileStorage));
      Spark.get("view-event", new ViewEventHandler(eventsStorage));
      Spark.get("view-profile", new ViewProfileHandler(profileStorage));
      Spark.get("change-attendance", new ChangeAttendanceHandler(profileStorage));
      Spark.get("respond-to-friend-request", new RespondToFriendRequestHandler(friendsStorage));
      Spark.get("unfriend", new UnfriendHandler(friendsStorage));
      Spark.get("view-friends", new ViewFriendsHandler(friendsStorage));
      Spark.get(
          "get-received-friend-requests", new GetReceivedFriendRequestsHandler(friendsStorage));
      Spark.get(
          "get-outgoing-friend-requests", new GetOutgoingFriendRequestsHandler(friendsStorage));
      Spark.get("rank-events-by-friend", new MostAttendedEventsFriendHandler(profileStorage));

      Spark.get("delete-all", new DeleteAllHandler(generalStorage));
      Spark.get("get-non-friends", new GetNonFriendsHandler(friendsStorage));
      Spark.get("get-event-history", new GetEventHistoryHandler(profileStorage));
      Spark.get("add-event-history", new AddEventHistoryHandler(profileStorage));
      Spark.get("remove-event-history", new RemoveEventHistoryHandler(profileStorage));
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
