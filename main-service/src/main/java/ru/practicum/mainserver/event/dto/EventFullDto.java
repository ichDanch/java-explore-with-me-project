package ru.practicum.mainserver.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainserver.category.dto.CategoryDto;
import ru.practicum.mainserver.comment.dto.CommentDtoOut;
import ru.practicum.mainserver.event.model.Location;
import ru.practicum.mainserver.user.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String created;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private String published;

    private boolean requestModeration;

    private String state;

    private String title;

    private int views;
    @Builder.Default
    private List<CommentDtoOut> comments = new ArrayList<>();
}
