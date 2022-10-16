package ru.practicum.mainserver.event;

import ru.practicum.mainserver.category.CategoryMapper;
import ru.practicum.mainserver.event.dto.EventFullDto;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.dto.NewEventDto;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.user.UserMapper;

import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static EventShortDto toShortDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventFullDto toFullDto(Event event) {
        if (event == null) {
            return null;
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .paid(event.isPaid())
                .title(event.getTitle())
                .created(event.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(event.getDescription())
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .published(event.getPublished() == null ? null : event.getPublished().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requestModeration(event.isRequestModeration())
                .state(event.getState().toString())
                .build();
    }


    public static Event toEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .location(newEventDto.getLocation())
                .title(newEventDto.getTitle())
                .build();
    }
}
