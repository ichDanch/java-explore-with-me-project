package ru.practicum.mainserver.compilation.Dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainserver.event.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    @NotNull
    private String title;
}
