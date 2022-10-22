package ru.practicum.mainserver.compilation;

import ru.practicum.mainserver.compilation.Dto.CompilationDto;
import ru.practicum.mainserver.compilation.Dto.NewCompilationDto;
import ru.practicum.mainserver.compilation.model.Compilation;
import ru.practicum.mainserver.event.EventMapper;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }

        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        List<EventShortDto> eventShortDtos = compilation.getEvents()
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());


        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(eventShortDtos)
                .build();
    }

    public static NewCompilationDto toNewDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        List<Long> eventIds = compilation.getEvents()
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        return NewCompilationDto.builder()
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(eventIds)
                .build();
    }
}
