
import edu.brown.cs.student.main.server.Events.Event;

import java.util.ArrayList;
import java.util.List;

public class TestEventFactory {
  public static List<Event> generateDummyEvents(int count) {
    List<Event> events = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      events.add(new Event(
          List.of("Event", String.valueOf(i)),
          List.of("Description", String.valueOf(i)),
          "2025-07-" + String.format("%02d", i + 1),
          "10:00",
          "10:00",
          List.of("tag" + i),
          1, null
      ));
    }
    return events;
  }
}
