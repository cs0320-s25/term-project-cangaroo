package edu.brown.cs.student.main.server.Events;

import java.util.List;

public record Event(
    List<String> name,
    List<String> description,
    String date,
    String startTime,
    String endTime,
    List<String> tags,
    int eventID,
    String eventOrganizer) {}

// event ID (int) x
// date (string) x
// description (string) x
// end time (String" x
// event organizer (string) x
// name (string) x
// start time (string) x
// tags (list of strings) x
// user id (string)
// users attending (list of strings)
