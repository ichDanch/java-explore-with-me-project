package ru.practicum.mainserver.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainserver.category.CategoryServiceImpl;
import ru.practicum.mainserver.category.model.Category;
import ru.practicum.mainserver.comment.CommentMapper;
import ru.practicum.mainserver.comment.CommentRepository;
import ru.practicum.mainserver.comment.dto.CommentDtoIn;
import ru.practicum.mainserver.comment.dto.CommentDtoOut;
import ru.practicum.mainserver.comment.model.Comment;
import ru.practicum.mainserver.event.EventMapper;
import ru.practicum.mainserver.event.Repository.EventRepository;
import ru.practicum.mainserver.event.Service.EventServiceImpl;
import ru.practicum.mainserver.event.Service.LocationService;
import ru.practicum.mainserver.event.dto.EventFullDto;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.dto.NewEventDto;
import ru.practicum.mainserver.event.dto.UpdateEventRequest;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.event.model.EventState;
import ru.practicum.mainserver.event.model.Location;
import ru.practicum.mainserver.exception.exceptions.EventNotFoundException;
import ru.practicum.mainserver.exception.exceptions.UserNotFoundException;
import ru.practicum.mainserver.exception.exceptions.ValidationException;
import ru.practicum.mainserver.request.RequestRepository;
import ru.practicum.mainserver.user.dto.UserDto;
import ru.practicum.mainserver.user.model.User;
import ru.practicum.mainserver.user.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl {
    private final UsersRepository usersRepository;
    private final CategoryServiceImpl categoryService;
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final EventServiceImpl eventService;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository,
                           CategoryServiceImpl categoryService,
                           EventRepository eventRepository,
                           LocationService locationService,
                           EventServiceImpl eventService,
                           RequestRepository requestRepository,
                           CommentRepository commentRepository) {
        this.usersRepository = usersRepository;
        this.categoryService = categoryService;
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.eventService = eventService;
        this.requestRepository = requestRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User savedUser = usersRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    public List<UserDto> findAllUsers(int from, int size, Collection<Long> ids) {
        if (from < 0 || size < 0) {
            throw new ValidationException("from or size are not valid" +
                    "Method [getAllUsers] class [UserServiceImpl] " + " from = " + from + " size = " + size);
        }

        Pageable pageWithElements = PageRequest.of(from / size, size, Sort.by("id"));

        if (ids.isEmpty()) {
            return usersRepository.findAll(pageWithElements)
                    .stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
        return usersRepository.findAllByIdIn(ids, pageWithElements)
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        usersRepository.deleteById(userId);
    }

    public User getUser(Long userId) {
        return usersRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<EventShortDto> findAllEventsCurrentUser(int from, int size, Long userId) {
        User user = getUser(userId);
        Pageable pageWithElements = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageWithElements);

        return page.stream()
                .map(EventMapper::toShortDto)
                .map(eventService::setConfirmedRequestsEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto patchEvent(UpdateEventRequest updateEventRequest, Long userId) {
        User user = getUser(userId);
        Event event = eventService.getEvent(updateEventRequest.getEventId());
        LocalDateTime now = LocalDateTime.now();

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException(event.getId());
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Wrong Event State. Method [patchEvent] Class [UserServiceImpl]");
        }
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            Category category = categoryService.getCategory(updateEventRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventRequest.getEventDate() != null) {
            LocalDateTime eventDate = updateEventRequest.getEventDate();
            if (eventDate.isBefore(now.minusHours(2))) {
                throw new ValidationException("Wrong date. Method [patchEvent] Class [UserServiceImpl]");
            } else {
                event.setEventDate(eventDate);
            }
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(savedEvent);
        return eventService.setConfirmedRequestsEventFullDto(eventFullDto);

    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime now = LocalDateTime.now();

        if (newEventDto.getEventDate().isBefore(now.minusHours(2))) {
            throw new ValidationException("Wrong date. Method [createEvent], class [UserServiceImpl]");
        }

        Location location = newEventDto.getLocation();
        location = locationService.save(location);

        User user = getUser(userId);
        Category category = categoryService.getCategory(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);

        event.setCreated(now);
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(EventState.PENDING);

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toFullDto(savedEvent);
    }

    public EventFullDto findEventCurrentUser(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = eventService.getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException(eventId);
        }
        EventFullDto eventFullDto = EventMapper.toFullDto(event);
        return eventService.setConfirmedRequestsEventFullDto(eventFullDto);
    }

    public EventFullDto cancelEventCurrentUser(Long userId, Long eventId) {
        Event event = eventService.getEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException(eventId);
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("May cancel only Pending events");
        }

        event.setState(EventState.CANCELED);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(event);
        return eventService.setConfirmedRequestsEventFullDto(eventFullDto);
    }

    @Transactional
    public CommentDtoOut createComment(Long userId, Long eventId, CommentDtoIn commentDtoIn) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        User user = getUser(userId);
        Event event = eventService.getEvent(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Event not published yet");
        }
        Comment comment = Comment.builder()
                .text(commentDtoIn.getText())
                .commentator(user)
                .event(event)
                .created(now).build();

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toDtoOut(savedComment);
    }

    @Transactional
    public CommentDtoOut patchComment(Long userId, Long commentId, CommentDtoIn commentDtoIn) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        if (!Objects.equals(userId, comment.getId())) {
            throw new ValidationException("User is not commentator");
        }
        comment.setText(commentDtoIn.getText());
        return CommentMapper.toDtoOut(comment);
    }

    public void deleteComment(Long userId, Long eventId, Long commentId) {
        User user = getUser(userId);
        Event event = eventService.getEvent(eventId);
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new UserNotFoundException(commentId));
    }
}
