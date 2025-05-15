package edu.brown.cs.student.main.server.Exceptions;

/** Exception thrown when a user tries to RSVP for an event that they are already attending */
public class EventAlreadyAttendingException extends Exception {

  public EventAlreadyAttendingException(String message) {
    super(message);
  }
}
