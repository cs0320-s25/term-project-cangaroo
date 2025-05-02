import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.HandlerLogic.Search;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SearchTest {

  // checks to make sure that the search algorithm searches for all versions of run in event name
  @Test
  public void testStemmedNameMatch() {
    Event e1 =
        new Event(
            List.of("Running", "Club"),
            List.of("Join", "us", "for", "a", "fun", "run"),
            "2025-06-01",
            "10:00",
            "10:00",
            List.of("exercise", "fitness"),
            1,
            "Brown Running Club");
    Event e2 =
        new Event(
            List.of("Cooking", "Class"),
            List.of("Learn", "to", "bake"),
            "2025-06-02",
            "18:00",
            "10:00",
            List.of("food", "hobby"),
            2,
            "Noodle Society");
    Search search = new Search();

    List<Event> results = search.getSearchedEvents(List.of("run"), List.of(e1, e2));

    assertEquals(1, results.size());
    assertEquals(e1, results.get(0));
  }

  // checks to make sure that the search algorithm searches for all versions of jump in event
  // description
  @Test
  public void testStemmedDescriptionMatch() {
    Event e1 =
        new Event(
            List.of("Morning", "Stretch"),
            List.of("We’ll", "be", "jumping", "and", "dancing"),
            "2025-06-03",
            "08:00",
            "10:00",
            List.of("yoga"),
            1,
            "Yoga Club");
    Event e2 =
        new Event(
            List.of("Poetry", "Night"),
            List.of("Spoken", "word", "and", "verse"),
            "2025-06-04",
            "20:00",
            "10:00",
            List.of("creative"),
            2,
            "Poetry Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("jump"), List.of(e1, e2));

    assertEquals(1, results.size());
    assertEquals(e1, results.get(0));
  }

  // checks to make sure that the search algorithm searches for all versions of kick in event tag
  @Test
  public void testStemmedTagMatch() {
    Event e1 =
        new Event(
            List.of("Football", "Game"),
            List.of("Exciting", "match"),
            "2025-06-05",
            "15:00",
            "10:00",
            List.of("sport", "kicking"),
            1,
            "Taekwondo");
    Event e2 =
        new Event(
            List.of("Art", "Workshop"),
            List.of("Draw", "and", "paint"),
            "2025-06-06",
            "13:00",
            "10:00",
            List.of("creative", "sketching"),
            2,
            "Art Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("kick"), List.of(e1, e2));

    assertEquals(1, results.size());
    assertEquals(e1, results.get(0));
  }

  // checks to make sure that the search algorithm handles no matches correctly
  @Test
  public void testNoMatch() {
    Event e1 =
        new Event(
            List.of("Chess", "Tournament"),
            List.of("Intense", "games"),
            "2025-06-09",
            "17:00",
            "10:00",
            List.of("boardgames"),
            1,
            "Board Game Club");
    Event e2 =
        new Event(
            List.of("Math", "Club"),
            List.of("Problem-solving", "sessions"),
            "2025-06-10",
            "16:00",
            "10:00",
            List.of("STEM"),
            2,
            "Math Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("swim"), List.of(e1, e2));

    assertTrue(results.isEmpty());
  }

  // checks to make sure that when an event matches multiple input words, it's prioritized
  @Test
  public void testMultipleInputWords() {
    Event e1 =
        new Event(
            List.of("Track", "Practice"),
            List.of("Running", "and", "jumping", "drills"),
            "2025-06-11",
            "09:00",
            "10:00",
            List.of("athletics"),
            1,
            "Club Soccer");
    Event e2 =
        new Event(
            List.of("Dance", "Class"),
            List.of("Hip", "hop", "and", "jumping"),
            "2025-06-12",
            "14:00",
            "10:00",
            List.of("movement"),
            2,
            "Dance Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("run", "jump"), List.of(e1, e2));

    assertEquals(2, results.size());
    assertEquals(e1, results.get(0)); // Higher score for multiple matches
    assertEquals(e2, results.get(1));
  }

  // checks to make sure that all synonyms of an input are found
  @Test
  public void testSynonymMatchingSport() {
    Event e1 =
        new Event(
            List.of("Morning", "Run"),
            List.of("Let's", "go", "for", "a", "quick", "run", "in", "the", "park"),
            "2025-06-10",
            "10:00",
            "08:00",
            List.of("exercise", "health"),
            1,
            "Brown Running Club");
    Event e2 =
        new Event(
            List.of("Book", "Club"),
            List.of("Discussing", "novels"),
            "2025-06-11",
            "17:00",
            "10:00",
            List.of("reading"),
            2,
            "Book Club");
    Event e3 =
        new Event(
            List.of("Sprint", "Practice"),
            List.of("Athletic", "running", "drills"),
            "2025-06-12",
            "10:00",
            "10:00",
            List.of("track"),
            3,
            "Exercise Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("sport"), List.of(e1, e2, e3));

    assertTrue(results.contains(e1));
    assertTrue(results.contains(e3));
    assertFalse(results.contains(e2));
  }

  // checks to make sure that all synonyms of an input are found
  @Test
  public void testSynonymMatchingCooking() {
    Event e1 =
        new Event(
            List.of("Make", "Dinner"),
            List.of(
                "Let's", "meet", "to", "practice", "culinary", "skills", "in", "the", "kitchen"),
            "2025-06-10",
            "08:00",
            "10:00",
            List.of("food", "health"),
            1,
            "Cooking Club");
    Event e2 =
        new Event(
            List.of("Book", "Club"),
            List.of("Discussing", "novels"),
            "2025-06-11",
            "17:00",
            "10:00",
            List.of("reading"),
            2,
            "Book Club");
    Event e3 =
        new Event(
            List.of("Sprint", "Practice"),
            List.of("Athletic", "running", "drills"),
            "2025-06-12",
            "10:00",
            "10:00",
            List.of("track"),
            3,
            "Running Club");

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("cooking"), List.of(e1, e2, e3));

    assertTrue(results.contains(e1));
    assertFalse(results.contains(e3));
    assertFalse(results.contains(e2));
  }

  @Test
  public void testNullSearch() {

    Search search = new Search();
    List<Event> results = search.getSearchedEvents(List.of("cooking"), List.of());
    assertTrue(results.isEmpty());
  }
}
