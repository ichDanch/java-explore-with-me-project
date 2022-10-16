package ru.practicum.mainserver.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
    private String reason;
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String message, List<String> errors, String reason) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String message, String error, String reason) {
        super();
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(error);
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }
}
