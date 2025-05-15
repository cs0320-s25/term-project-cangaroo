package edu.brown.cs.student.main.server.Exceptions;


/**
 * Exception thrown when trying to unfriend two users that aren't friends
 */
public class NotFriendsException extends Exception {
  public NotFriendsException(String message) {
    super(message);
  }
}
