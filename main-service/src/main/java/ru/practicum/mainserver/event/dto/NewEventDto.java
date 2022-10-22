
package ru.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainserver.event.model.Location;
import ru.practicum.mainserver.validation.CreateValidation;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
public class NewEventDto {

    @NotNull(groups = CreateValidation.class)
    @NotBlank(groups = CreateValidation.class)
    private String annotation;

    @NotNull(groups = CreateValidation.class)
    private Long category;

    @NotNull(groups = CreateValidation.class)
    @NotBlank(groups = CreateValidation.class)
    private String description;

    @Future
    @NotNull(groups = CreateValidation.class)
    private LocalDateTime eventDate;

    @NotNull(groups = CreateValidation.class)
    private Location location;

    private Boolean paid;

    @Builder.Default
    @PositiveOrZero
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotNull(groups = CreateValidation.class)
    private String title;

}
