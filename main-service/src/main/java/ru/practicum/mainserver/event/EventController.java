package ru.practicum.mainserver.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainserver.event.Service.EventServiceImpl;
import ru.practicum.mainserver.event.dto.AdminUpdateEventRequest;
import ru.practicum.mainserver.event.dto.EventFullDto;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
public class EventController {
    private final EventServiceImpl eventService;

    @Autowired
    public EventController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    /**
     * Public: События
     * Публичный API для работы с событиями
     */
    @GetMapping("/events")
    public List<EventShortDto> findAllEventsPublic(
            @RequestParam(defaultValue = "") String text,
            @RequestParam Set<Long> categories,
            @RequestParam Boolean paid,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam Boolean onlyAvailable,
            @RequestParam EventSort sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        return eventService.findAllEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@PathVariable(value = "id") Long eventId, HttpServletRequest request) {

        return eventService.findEventById(eventId, request);
    }

    /**
     * Admin: События
     * API для работы с событиями
     */

    @GetMapping("/admin/events")
    public List<EventFullDto> findAllEventsAdmin(
            @RequestParam Set<Long> users,
            @RequestParam Set<EventState> states,
            @RequestParam List<Long> categories,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findAllEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(@RequestBody AdminUpdateEventRequest adminUpdateEventRequest,
                                         @PathVariable @Valid Long eventId) {
        return eventService.updateEventAdmin(adminUpdateEventRequest, eventId);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    public EventFullDto publishEventAdmin(@PathVariable Long eventId) {

        return eventService.publishEventAdmin(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto rejectEventAdmin(@PathVariable Long eventId) {

        return eventService.rejectEventAdmin(eventId);
    }


}
