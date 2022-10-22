package ru.practicum.statserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statserver.dto.EndpointHitDto;
import ru.practicum.statserver.dto.ViewStats;

import java.util.List;

@RestController
@Slf4j
public class StatsController {

    private final StatsServiceImpl statsService;

    @Autowired
    public StatsController(StatsServiceImpl statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public EndpointHitDto createEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("createEndpointHit, {}", endpointHitDto);
        return statsService.saveEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> findViewStats(@RequestParam String start,
                                         @RequestParam String end,
                                         @RequestParam List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("getStats, {},{},{},{}", start, end, uris, unique);
        return statsService.findViewStats(start, end, uris, unique);
    }
}
