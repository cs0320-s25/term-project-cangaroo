package edu.brown.cs.student.main.server.Exceptions;


/**
 * Exception thrown when a profile isn't found
 */
public class NoProfileFoundException extends Exception {
  public NoProfileFoundException(String message) {
    super(message);
  }
}
