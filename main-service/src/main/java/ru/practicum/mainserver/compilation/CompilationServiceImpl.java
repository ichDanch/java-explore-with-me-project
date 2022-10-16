package ru.practicum.mainserver.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainserver.compilation.Dto.CompilationDto;
import ru.practicum.mainserver.compilation.Dto.NewCompilationDto;
import ru.practicum.mainserver.compilation.model.Compilation;
import ru.practicum.mainserver.event.Service.EventServiceImpl;
import ru.practicum.mainserver.event.dto.EventShortDto;
import ru.practicum.mainserver.event.model.Event;
import ru.practicum.mainserver.exception.exceptions.CompilationNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl {
    private final CompilationRepository compilationRepository;
    private final EventServiceImpl eventService;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventServiceImpl eventService) {
        this.compilationRepository = compilationRepository;
        this.eventService = eventService;
    }


    public List<CompilationDto> findAllCompilations(Integer from, Integer size, boolean pinned) {
        Pageable pageWithElements = PageRequest.of(from / size, size, Sort.by("id"));

        Page<Compilation> page = compilationRepository.findCompilationsByPinned(pinned, pageWithElements);

        return page.stream()
                .map(CompilationMapper::toDto)
                .map(this::setConfirmedRequestsAndViewsCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = getCompilation(compId);
        CompilationDto compilationDto = CompilationMapper.toDto(compilation);
        return setConfirmedRequestsAndViewsCompilationDto(compilationDto);
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        List<Long> eventsIds = newCompilationDto.getEvents();

        List<Event> events = eventsIds.stream()
                .map(eventService::getEvent)
                .collect(Collectors.toList());

        compilation.setEvents(events);
        Compilation savedCompilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = CompilationMapper.toDto(savedCompilation);

        List<EventShortDto> eventShortDtos = compilationDto.getEvents()
                .stream()
                .map(eventService::setConfirmedRequestsEventShortDto)
                .collect(Collectors.toList());

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        getCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilation(compId);
        Event event = eventService.getEvent(eventId);
        List<Event> events = compilation.getEvents();

        events.remove(event);

        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    @Transactional
    public CompilationDto addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilation(compId);
        Event event = eventService.getEvent(eventId);
        List<Event> events = compilation.getEvents();

        events.add(event);

        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    public CompilationDto unpinCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilation.setPinned(false);
        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    public CompilationDto pinCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilation.setPinned(true);
        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(savedCompilation);
    }

    public Compilation getCompilation(long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new CompilationNotFoundException(id));
    }

    public CompilationDto setConfirmedRequestsAndViewsCompilationDto(CompilationDto compilationDto) {
        List<EventShortDto> eventShortDtos = compilationDto.getEvents()
                .stream()
                .map(eventService::setConfirmedRequestsEventShortDto)
                .collect(Collectors.toList());
        compilationDto.setEvents(eventShortDtos);
        return compilationDto;
    }
}
