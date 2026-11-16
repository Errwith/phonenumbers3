package ru.university.budakov15.exception;

public class InvalidNameFormatException extends BusinessException {
  public InvalidNameFormatException(String message) {
    super(message);
  }
}