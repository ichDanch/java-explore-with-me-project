package ru.practicum.mainserver.exception.exceptions;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(Long userId) {
        super(String.format("Compilation with id %d not found", userId));
    }
}
