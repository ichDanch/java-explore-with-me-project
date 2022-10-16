package ru.practicum.mainserver.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.mainserver.client.dto.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stat_service_url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveEndpointHit(EndpointHit endpointHit) {
        try {
            return post("/hit", endpointHit);
        } catch (Exception e) {
            log.error("Method [saveEndpointHit] class [StatsClient] POST error: " + e.getMessage());
        }
        return null;
    }

    public ResponseEntity<Object> findViewStats(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uris,
                                                Boolean unique) {
        String startRange = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endRange = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> parameters = Map.of(
                "start", startRange,
                "end", endRange,
                "uris", uris.get(0),
                "unique", unique
        );

        try {
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } catch (Exception e) {
            log.error("Method [findViewStats] class [StatsClient] Get error: " + e.getMessage());
        }
        return null;
    }
}
