package ru.practicum.mainserver.event.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select event from Event AS event" +
            " where (:text) is null or upper (event.annotation) like upper(concat('%', :text, '%'))" +
            "or upper(event.description) like upper(concat('%', :text, '%'))" +
            "and  (:categories is null or event.category.id in :categories)" +
            "and (:paid is null or event.paid = :paid)" +
            "and (event.eventDate >= :start)" +
            "and (event.eventDate <= :end)")
    Page<Event> findAllEvents(String text, Set<Long> categories, Boolean paid, LocalDateTime start,
                              LocalDateTime end, Pageable pageable);

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
