package ru.university.budakov15.exception;

public class EmptyLastNameException extends BusinessException {
    public EmptyLastNameException(String message) {
        super(message);
    }
}
