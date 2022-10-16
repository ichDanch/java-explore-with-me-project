package ru.practicum.mainserver.request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainserver.event.Service.EventServiceImpl;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.event.model.EventState;
import ru.practicum.mainserver.exception.exceptions.EventNotFoundException;
import ru.practicum.mainserver.exception.exceptions.RequestNotFoundException;
import ru.practicum.mainserver.exception.exceptions.ValidationException;
import ru.practicum.mainserver.request.dto.ParticipationRequestDto;
import ru.practicum.mainserver.request.model.ParticipationRequest;
import ru.practicum.mainserver.request.model.RequestStatus;
import ru.practicum.mainserver.user.UserServiceImpl;
import ru.practicum.mainserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class RequestServiceImpl {
    private final RequestRepository requestRepository;
    private final EventServiceImpl eventService;
    private final UserServiceImpl userService;

    public RequestServiceImpl(RequestRepository requestRepository, EventServiceImpl eventService, UserServiceImpl userService) {
        this.requestRepository = requestRepository;
        this.eventService = eventService;
        this.userService = userService;
    }


    public List<ParticipationRequestDto> findParticipationRequestsByCurrentUser(Long userId, Long eventId) {
        Event event = eventService.getEvent(eventId);
        User requester = userService.getUser(userId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventNotFoundException(eventId);
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

    }

    @Transactional
    public ParticipationRequestDto confirmParticipationRequest(Long userId, Long eventId, Long requestId) {
        User user = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);
        ParticipationRequest request = getRequest(requestId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User not owner. Method [confirmParticipationRequest]. Class [RequestServiceImpl]");
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ValidationException("Status not pending. Method [confirmParticipationRequest]. Class [RequestServiceImpl]");
        }

        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() >= confirmedRequests) {
            request.setStatus(RequestStatus.REJECTED);
        }
        request.setStatus(RequestStatus.CONFIRMED);
        ParticipationRequest confirmedRequest = requestRepository.save(request);
        return RequestMapper.toDto(confirmedRequest);
    }

    @Transactional
    public ParticipationRequestDto rejectParticipationRequest(Long userId, Long eventId, Long requestId) {
        User user = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);
        ParticipationRequest request = getRequest(requestId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User not owner. Method [rejectParticipationRequest]. Class [RequestServiceImpl]");
        }
        request.setStatus(RequestStatus.REJECTED);
        ParticipationRequest savedRequest = requestRepository.save(request);

        return RequestMapper.toDto(savedRequest);

    }

    public List<ParticipationRequestDto> findUserRequests(Long userId) {
        User user = userService.getUser(userId);

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto createRequestCurrentUser(Long userId, Long eventId) {
        User requester = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);
        ParticipationRequest request = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (request != null) {
            throw new ValidationException("request is already exist.  Method [createRequestCurrentUser] class [UserServiceImpl]");
        }
        if (!(event.getState().equals(EventState.PUBLISHED))) {
            throw new ValidationException("request on not publish event. Method [createRequestCurrentUser] class [UserServiceImpl]");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("requester not may initiator.  Method [createRequestCurrentUser] class [UserServiceImpl]");
        }

        Integer confirmedRequests = requestRepository.countDistinctByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0
                && event.getParticipantLimit() <= confirmedRequests) {
            throw new ValidationException("Wrong ParticipantLimit. Method [createRequestCurrentUser] class [UserServiceImpl]");
        }

        ParticipationRequest createdRequest = new ParticipationRequest();
        createdRequest.setCreated(LocalDateTime.now());
        createdRequest.setEvent(event);
        createdRequest.setRequester(requester);

        if (event.isRequestModeration()) {
            createdRequest.setStatus(RequestStatus.PENDING);
        } else {
            createdRequest.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipationRequest savedRequest = requestRepository.save(createdRequest);
        return RequestMapper.toDto(savedRequest);

    }

    @Transactional
    public ParticipationRequestDto cancelYourRequest(Long userId, Long requestId) {
        User user = userService.getUser(userId);
        ParticipationRequest request = getRequest(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new RequestNotFoundException(requestId);
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest savedRequest = requestRepository.save(request);
        return RequestMapper.toDto(savedRequest);
    }

    private ParticipationRequest getRequest(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new RequestNotFoundException(id));
    }
}
