package ru.practicum.mainserver.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainserver.comment.dto.CommentDtoIn;
import ru.practicum.mainserver.comment.dto.CommentDtoOut;
import ru.practicum.mainserver.event.dto.EventFullDto;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.dto.NewEventDto;
import ru.practicum.mainserver.event.dto.UpdateEventRequest;
import ru.practicum.mainserver.request.RequestServiceImpl;
import ru.practicum.mainserver.request.dto.ParticipationRequestDto;
import ru.practicum.mainserver.user.dto.UserDto;
import ru.practicum.mainserver.validation.CreateValidation;
import ru.practicum.mainserver.validation.PatchValidation;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;


@Slf4j
@Validated
@RestController
public class UserController {
    private final UserServiceImpl userService;
    private final RequestServiceImpl requestService;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl, RequestServiceImpl requestService) {
        this.userService = userServiceImpl;
        this.requestService = requestService;
    }

    /**
     * Admin: Пользователи
     * API для работы с пользователями
     */

    /**
     * Получение информации о пользователях
     */
    @PostMapping("/admin/users")
    @Validated(CreateValidation.class)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.saveUser(userDto);
    }

    /**
     * Добавление нового пользователя
     */
    @GetMapping("/admin/users")
    public List<UserDto> findAllUsers(@RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam Set<Long> ids) {
        return userService.findAllUsers(from, size, ids);
    }

    /**
     * Удаление пользователя
     */
    @DeleteMapping("/admin/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    /**
     * Private: События
     * Закрытый API для работы с событиями
     */

    /**
     * Получние событий добавленных текущим пользователем
     */
    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> findAllEventsCurrentUser(@RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @PathVariable Long userId) {
        return userService.findAllEventsCurrentUser(from, size, userId);
    }

    /**
     * Изменение события добавленного текущим пользователем
     */
    @PatchMapping("/users/{userId}/events")
    @Validated(PatchValidation.class)
    public EventFullDto patchEvent(@RequestBody @Valid UpdateEventRequest updateEventRequest,
                                   @PathVariable Long userId) {
        return userService.patchEvent(updateEventRequest, userId);
    }

    /**
     * Добавление нового события
     */
    @PostMapping("/users/{userId}/events")
    @Validated(CreateValidation.class)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return userService.createEvent(userId, newEventDto);
    }

    /**
     * Получние полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto findEventCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return userService.findEventCurrentUser(userId, eventId);
    }

    /**
     * Отмена события добаленного текущим пользователм
     */
    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEventCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return userService.cancelEventCurrentUser(userId, eventId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     */
    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findParticipationRequestsByCurrentUser(@PathVariable Long userId,
                                                                                @PathVariable Long eventId) {
        return requestService.findParticipationRequestsByCurrentUser(userId, eventId);
    }

    /**
     * Подтверждение чужой заявки на участие в событии текущего пользователя
     */
    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipationRequest(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @PathVariable Long reqId) {
        return requestService.confirmParticipationRequest(userId, eventId, reqId);
    }

    /**
     * Отклонение чужой заявки на участие в событии текущего пользователя
     */
    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @PathVariable Long reqId) {
        return requestService.rejectParticipationRequest(userId, eventId, reqId);
    }

    /**
     * Создать комментарий
     */
    @PostMapping("/users/{userId}/events/{eventId}/comment")
    public CommentDtoOut createComment(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @RequestBody @Valid CommentDtoIn commentDtoIn) {
        log.info("userId = {}, eventId = {}, commentDtoId = {}", userId, eventId, commentDtoIn);
        return userService.createComment(userId, eventId, commentDtoIn);
    }

    /**
     * Создать комментарий
     */
    @PatchMapping("/users/{userId}/events/{eventId}/comment/{commentId}")
    public CommentDtoOut patchComment(@PathVariable long userId,
                                      @PathVariable long commentId,
                                      @RequestBody @Valid CommentDtoIn commentDtoIn) {
        return userService.patchComment(userId, commentId, commentDtoIn);
    }

    /**
     * Удалить комментарий
     */
    @DeleteMapping("/users/{userId}/events/{eventId}/comment/{commentId}")
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long eventId,
                              @PathVariable long commentId) {
        userService.deleteComment(userId, eventId, commentId);
    }


    /**
     Private: Запросы на участие
     Закрытый API для работы с запросами текущего пользователя на участие в событиях
     */

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     */
    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> findUserRequests(@PathVariable Long userId) {
        return requestService.findUserRequests(userId);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     */
    @PostMapping("/users/{userId}/requests")
    @Validated(CreateValidation.class)
    public ParticipationRequestDto createRequestCurrentUser(@PathVariable Long userId,
                                                            @RequestParam Long eventId) {
        return requestService.createRequestCurrentUser(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelYourRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        return requestService.cancelYourRequest(userId, requestId);
    }

}
