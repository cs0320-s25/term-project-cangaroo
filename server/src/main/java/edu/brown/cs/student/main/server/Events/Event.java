package edu.brown.cs.student.main.server.Events;

import java.util.List;

/**
 * Record that contains details for an event
 * @param name - name of the event
 * @param description - description for the event
 * @param date - date of the event in YYYY/MM/DD
 * @param startTime - start time for the event in military time
 * @param endTime - end time for the event in military time
 * @param tags - list of tags that the event has
 * @param eventID - ID of the event
 * @param eventOrganizer - name of the organizer of the event
 */
public record Event(
    List<String> name,
    List<String> description,
    String date,
    String startTime,
    String endTime,
    List<String> tags,
    int eventID,
    String eventOrganizer) {}
