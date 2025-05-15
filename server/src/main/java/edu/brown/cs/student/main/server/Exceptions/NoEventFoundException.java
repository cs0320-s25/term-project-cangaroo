package edu.brown.cs.student.main.server.Exceptions;

/**
 * Exception thrown when an event is not found
 */
public class NoEventFoundException extends Exception {
  public NoEventFoundException(String message) {
    super(message);
  }
}
