package edu.brown.cs.student.main.server.Profiles;

import edu.brown.cs.student.main.server.Events.Event;
import java.util.List;

public record Profile(
    String username,
    List<String> interestedTags,
    List<String> friendNames,
    List<Event> eventsAttending) {}
