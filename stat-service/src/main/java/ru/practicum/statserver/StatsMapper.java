package ru.practicum.statserver;

import ru.practicum.statserver.dto.EndpointHitDto;
import ru.practicum.statserver.model.EndpointHit;


public class StatsMapper {

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }

        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        if (endpointHitDto == null) {
            return null;
        }

        return EndpointHit.builder()
                .id(endpointHitDto.getId())
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
