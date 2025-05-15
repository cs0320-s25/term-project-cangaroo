package edu.brown.cs.student.main.server.Exceptions;

/**
 * Exception thrown when trying to respond to a friend request that doesn't exist
 */
public class NoExistingFriendRequestException extends Exception {

  public NoExistingFriendRequestException(String message) {
    super(message);
  }
}
