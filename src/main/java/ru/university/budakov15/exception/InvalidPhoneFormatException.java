package ru.university.budakov15.exception;

public class InvalidPhoneFormatException extends BusinessException {
  public InvalidPhoneFormatException(String message) {
    super(message);
  }
}