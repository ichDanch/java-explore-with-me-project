package ru.practicum.mainserver.exception.exceptions;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long categoryId) {
        super(String.format("Request with id %d not found", categoryId));
    }
}
