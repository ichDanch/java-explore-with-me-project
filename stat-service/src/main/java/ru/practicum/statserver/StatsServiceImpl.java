package ru.practicum.statserver;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statserver.dto.EndpointHitDto;
import ru.practicum.statserver.dto.ViewStats;
import ru.practicum.statserver.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl {

    private final StatsRepository statsRepository;


    public EndpointHitDto saveEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = StatsMapper.toEndpointHit(endpointHitDto);
        EndpointHit savedEndpointHit = statsRepository.save(endpointHit);
        return StatsMapper.toDto(savedEndpointHit);
    }


    public List<ViewStats> findViewStats(String start,
                                         String end,
                                         List<String> uris,
                                         Boolean unique) {

        if (uris.isEmpty()) {
            throw new RuntimeException("Uris can not be null");
        }

        LocalDateTime rangeStart = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime rangeEnd = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStats> viewStatsList = new ArrayList<>();

        for (String uri : uris) {
            ViewStats viewStats = ViewStats.builder()
                    .app("main-service")
                    .uri(uri)
                    .build();
            if (unique) {
                viewStats.setHits(statsRepository.countDistinctByTimestampBetweenAndUri(rangeStart, rangeEnd, uri));
            } else {
                viewStats.setHits(statsRepository.countByTimestampBetweenAndUri(rangeStart, rangeEnd, uri));
            }
            viewStatsList.add(viewStats);
        }
        return viewStatsList;
    }
}
