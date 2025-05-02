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

