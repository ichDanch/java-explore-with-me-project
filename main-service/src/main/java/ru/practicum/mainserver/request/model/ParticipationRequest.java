package ru.practicum.mainserver.request.model;

import lombok.*;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime created;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User requester;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}

