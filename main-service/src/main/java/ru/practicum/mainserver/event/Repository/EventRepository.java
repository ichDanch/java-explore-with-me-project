package ru.practicum.mainserver.event.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event>
    findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateBetween(
            String annotation,
            String description,
            Set<Long> categoryId,
            boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    Page<Event>
    findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryIdInAndPaidAndEventDateAfter(
            String annotation,
            String description,
            Set<Long> categoryId,
            boolean paid,
            LocalDateTime now,
            Pageable pageable);


    Page<Event> findAllByAnnotationIgnoreCaseAndDescriptionIgnoreCaseAndCategoryIdInAndPaidAndEventDateBetween(
            String annotation,
            String description,
            Set<Long> category,
            boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);


    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateAfter(
            Set<Long> initiatorId,
            Set<EventState> state,
            List<Long> categoryId,
            LocalDateTime now,
            Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
            Set<Long> initiatorId,
            Set<EventState> state,
            List<Long> categoryId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
