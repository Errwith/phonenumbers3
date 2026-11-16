package ru.university.budakov15.exception;

public class DuplicatePhoneException extends BusinessException {
    public DuplicatePhoneException(String message) {
        super(message);
    }
}