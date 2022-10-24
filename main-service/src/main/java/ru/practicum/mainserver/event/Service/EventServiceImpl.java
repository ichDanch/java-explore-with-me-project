package ru.practicum.mainserver.event.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainserver.category.CategoryServiceImpl;
import ru.practicum.mainserver.category.model.Category;
import ru.practicum.mainserver.client.StatsClient;
import ru.practicum.mainserver.client.dto.EndpointHit;
import ru.practicum.mainserver.comment.CommentMapper;
import ru.practicum.mainserver.comment.CommentRepository;
import ru.practicum.mainserver.comment.dto.CommentDtoOut;
import ru.practicum.mainserver.comment.model.Comment;
import ru.practicum.mainserver.event.EventMapper;
import ru.practicum.mainserver.event.EventSort;
import ru.practicum.mainserver.event.Repository.EventRepository;
import ru.practicum.mainserver.event.dto.AdminUpdateEventRequest;
import ru.practicum.mainserver.event.dto.EventFullDto;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.event.model.EventState;
import ru.practicum.mainserver.exception.exceptions.EventNotFoundException;
import ru.practicum.mainserver.exception.exceptions.ValidationException;
import ru.practicum.mainserver.request.RequestRepository;
import ru.practicum.mainserver.request.model.RequestStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryServiceImpl categoryService;
    private final CommentRepository commentRepository;
    private final StatsClient statsClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            RequestRepository requestRepository,
                            CategoryServiceImpl categoryService,
                            CommentRepository commentRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.categoryService = categoryService;
        this.commentRepository = commentRepository;
        this.statsClient = statsClient;
    }

    public List<EventShortDto> findAllEvents(String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                             Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }
        Pageable page = PageRequest.of(from / size, size, Sort.by("id"));

        List<Event> events = eventRepository.findAllEvents(text, categories, paid, rangeStart, rangeEnd, page)
                .stream()
                .collect(Collectors.toList());

        if (sort == EventSort.EVENT_DATE) {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = events.stream()
                .filter(event -> event.getState() == EventState.PUBLISHED)
                .map(EventMapper::toShortDto)
                .map(this::setConfirmedRequestsEventShortDto)
                .map(this::setViewsEventShortDto)
                .map(this::setCommentsEventShortDto)
                .collect(Collectors.toList());

        if (sort == EventSort.VIEW) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        if (onlyAvailable) {
            eventShortDtos = eventShortDtos.stream()
                    .filter(eventShortDto ->
                            eventShortDto.getConfirmedRequests() <= getEvent(eventShortDto.getId()).getParticipantLimit())
                    .collect(Collectors.toList());
        }

        createEndpointHit(request);

        return eventShortDtos;
    }

    public EventFullDto findEventById(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException(eventId);
        }

        createEndpointHit(request);

        EventFullDto eventFullDto = EventMapper.toFullDto(event);
        eventFullDto = setConfirmedRequestsEventFullDto(eventFullDto);
        eventFullDto = setViewsEventFullDto(eventFullDto);
        eventFullDto = setCommentsEventFullDto(eventFullDto);

        return eventFullDto;
    }

    public List<EventFullDto> findAllEventsAdmin(
            Set<Long> users, Set<EventState> states, List<Long> categories, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Integer from, Integer size) {

        List<Event> events;
        Pageable pageWithElements = PageRequest.of(from / size, size, Sort.by("id"));

        if (rangeStart == null || rangeEnd == null) {
            events = eventRepository
                    .findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateAfter(
                            users, states, categories, LocalDateTime.now(), pageWithElements)
                    .stream()
                    .collect(Collectors.toList());


        } else {
            events = eventRepository
                    .findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                            users, states, categories, rangeStart, rangeEnd, pageWithElements)
                    .stream()
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(EventMapper::toFullDto)
                .map(this::setConfirmedRequestsEventFullDto)
                .map(this::setCommentsEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateEventAdmin(AdminUpdateEventRequest adminUpdateEventRequest, Long eventId) {

        Event event = getEvent(eventId);
        if (adminUpdateEventRequest.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }
        if (adminUpdateEventRequest.getCategory() != null) {
            Category category = categoryService.getCategory(adminUpdateEventRequest.getCategory());
            event.setCategory(category);
        }
        if (adminUpdateEventRequest.getDescription() != null) {
            event.setDescription(adminUpdateEventRequest.getDescription());
        }
        if (adminUpdateEventRequest.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(adminUpdateEventRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            event.setEventDate(eventDate);
            if (eventDate.isBefore(LocalDateTime.now().minusHours(2))) {
                throw new ValidationException("Wrong date. Method [updateEventAdmin], class [EventServiceImpl]");
            }
        }
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(adminUpdateEventRequest.getLocation());
        }
        if (adminUpdateEventRequest.getPaid() != null) {
            event.setPaid(adminUpdateEventRequest.getPaid());
        }
        if (adminUpdateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        }
        if (adminUpdateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }
        if (adminUpdateEventRequest.getTitle() != null) {
            event.setTitle(adminUpdateEventRequest.getTitle());
        }
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toFullDto(savedEvent);
    }

    @Transactional
    public EventFullDto publishEventAdmin(Long eventId) {
        LocalDateTime now = LocalDateTime.now();
        Event event = getEvent(eventId);

        if (event.getEventDate().isBefore(now.minusHours(2))) {
            throw new ValidationException("Wrong date. Method [publishEventAdmin], class [EventServiceImpl]");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Publish only pending. Method [publishEventAdmin], class [EventServiceImpl]");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublished(now);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(savedEvent);

        return setConfirmedRequestsEventFullDto(eventFullDto);
    }

    @Transactional
    public EventFullDto rejectEventAdmin(Long eventId) {

        Event event = getEvent(eventId);

        event.setState(EventState.CANCELED);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(savedEvent);
        return setConfirmedRequestsEventFullDto(eventFullDto);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public EventShortDto setConfirmedRequestsEventShortDto(EventShortDto eventShortDto) {
        long eventId = eventShortDto.getId();
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        eventShortDto.setConfirmedRequests(confirmedRequests);
        return eventShortDto;
    }

    public EventShortDto setViewsEventShortDto(EventShortDto eventShortDto) {
        int views = getViews(eventShortDto.getId());
        eventShortDto.setViews(views);
        return eventShortDto;
    }

    public EventFullDto setConfirmedRequestsEventFullDto(EventFullDto eventFullDto) {
        long eventId = eventFullDto.getId();
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }

    public EventFullDto setViewsEventFullDto(EventFullDto eventFullDto) {
        int views = getViews(eventFullDto.getId());
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    public EventShortDto setCommentsEventShortDto(EventShortDto eventShortDto) {
        Long eventId = eventShortDto.getId();
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        List<CommentDtoOut> commentDtoOuts = comments.stream().map(CommentMapper::toDtoOut).collect(Collectors.toList());
        eventShortDto.setComments(commentDtoOuts);
        return eventShortDto;
    }

    public EventFullDto setCommentsEventFullDto(EventFullDto eventFullDto) {
        Long eventId = eventFullDto.getId();
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        List<CommentDtoOut> commentDtoOuts = comments.stream().map(CommentMapper::toDtoOut).collect(Collectors.toList());
        eventFullDto.setComments(commentDtoOuts);
        return eventFullDto;
    }

    public int getViews(long eventId) {

        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        ResponseEntity<Object> responseEntity = statsClient.findViewStats(
                start, end, List.of("/events/" + eventId), false);

        if (responseEntity.getBody().equals("")) {
            Object body = ((LinkedHashMap) responseEntity.getBody()).get("hits");
            return (Integer) body;
        }
        return 0;
    }

    public void createEndpointHit(HttpServletRequest request) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            EndpointHit endpointHit = EndpointHit.builder()
                    .app("main_server")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(now)
                    .build();

            statsClient.saveEndpointHit(endpointHit);
        } catch (Exception e) {
            log.error("Method [createEndpointHit] class [EventServiceImpl] POST error: " + e.getMessage());
        }
    }
}
