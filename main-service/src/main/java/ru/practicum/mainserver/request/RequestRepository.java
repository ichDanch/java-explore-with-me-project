package ru.practicum.mainserver.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainserver.request.model.ParticipationRequest;
import ru.practicum.mainserver.request.model.RequestStatus;

import java.util.List;


public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    ParticipationRequest findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Integer countDistinctByEventIdAndStatus(Long eventId, RequestStatus status);
}
