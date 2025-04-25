import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import edu.brown.cs.student.main.server.HandlerLogic.MatchEvents;

import edu.brown.cs.student.main.server.Events.Event;
import edu.brown.cs.student.main.server.HandlerLogic.RandomMatch;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MatchTest {

  //tests that when you randomly generate events, you get no more than 10
  @Test
  public void testRandomSelectionSizeLimit() {
    List<Event> events = TestEventFactory.generateDummyEvents(25);
    RandomMatch randomMatch = new RandomMatch();
    List<Event> selected = randomMatch.getRandomEvent(events);

    assertEquals(10, selected.size(), "Should return 10 events at most");
  }

  //tests that if there are less than 10 events, all the events are automatically returned
  @Test
  public void testRandomReturnsAllIfLessThanTen() {
    List<Event> events = TestEventFactory.generateDummyEvents(5);
    RandomMatch randomMatch = new RandomMatch();
    List<Event> selected = randomMatch.getRandomEvent(events);

    assertEquals(5, selected.size(), "Should return all available events if fewer than 10");
    assertTrue(events.containsAll(selected), "All selected events must be from original list");
  }

  //regular stemming works for matches
  @Test
  public void testStemmedTagMatch() {
    // Create two events
    Event e1 = new Event(
        List.of("Running", "Club"),
        List.of("Join", "us", "for", "a", "fun", "run"),
        "2025-06-01",
        "10:00",
        "10:00",
        List.of("exercise", "fitness"),
        1);

    Event e2 = new Event(
        List.of("Cooking", "Class"),
        List.of("Learn", "to", "bake"),
        "2025-06-02",
        "18:00",
        "18:00",
        List.of("food", "hobby"),
        2);

    // This simulates a profile with interest tags like "run" (should match e1)
    List<String> profileTags = List.of("run");

    MatchEvents matcher = new MatchEvents();
    List<Event> results = matcher.getMatchedEvents(profileTags, List.of(e1, e2));

    // Assert only one event matched and it was e1
    assertEquals(1, results.size());
    assertEquals(e1, results.get(0));
  }

  @Test
  public void testSTEMMatch() {
    // Create two events
    Event e1 = new Event(
        List.of("Running", "Club"),
        List.of("Join", "us", "for", "a", "fun", "run"),
        "2025-06-01",
        "10:00",
        "10:00",
        List.of("exercise", "fitness"),
        1);

    Event e2 = new Event(
        List.of("Cooking", "Class"),
        List.of("Learn", "to", "bake"),
        "2025-06-02",
        "18:00",
        "18:00",
        List.of("food", "hobby"),
        2);

    Event e3 = new Event(
        List.of("Coding", "Class"),
        List.of("Learn", "to", "use", "computers", "to", "code"),
        "2025-06-02",
        "18:00",
        "18:00",
        List.of("technology", "STEM"),
        3);

    Event e4 = new Event(
        List.of("Pet", "Rocks"),
        List.of("Learn", "to", "get", "a", "pet", "rock"),
        "2025-06-02",
        "18:00",
        "18:00",
        List.of("pets", "rocks"),
        4);

    // This simulates a profile with interest tags like "run" (should match e1)
    List<String> profileTags = List.of("science", "biology", "computer science", "running", "jogging", "gym");

    MatchEvents matcher = new MatchEvents();
    List<Event> results = matcher.getMatchedEvents(profileTags, List.of(e1, e2, e3, e4));

    // Assert only one event matched and it was e1
    assertEquals(2, results.size());
    assertTrue(results.contains(e1));
    assertTrue(results.contains(e3));
    assertFalse(results.contains(e2));
    assertFalse(results.contains(e4));

  }



}
