package ru.practicum.statserver;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statserver.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    int countByUri(String uri);

    List<EndpointHit> findAllByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);

    Set<EndpointHit> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    int countDistinctByTimestampBetweenAndUri(LocalDateTime start, LocalDateTime end, String uri);

    int countByTimestampBetweenAndUri(LocalDateTime start, LocalDateTime end, String uri);
}
