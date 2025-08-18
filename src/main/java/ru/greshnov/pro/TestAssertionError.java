package ru.greshnov.pro;

public class TestAssertionError extends RuntimeException {
  public TestAssertionError(String message) {
    super(message);
  }
}
