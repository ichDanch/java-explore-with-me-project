package ru.practicum.mainserver.exception.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long userId) {
        super(String.format("Event with id %d not found", userId));
    }
}
