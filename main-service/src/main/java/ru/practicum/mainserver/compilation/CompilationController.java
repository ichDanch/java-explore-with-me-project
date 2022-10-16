package ru.practicum.mainserver.compilation;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainserver.compilation.Dto.CompilationDto;
import ru.practicum.mainserver.compilation.Dto.NewCompilationDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@Validated
public class CompilationController {

    private final CompilationServiceImpl compilationService;

    public CompilationController(CompilationServiceImpl compilationService) {
        this.compilationService = compilationService;
    }

    /**
     * Public: Подборки событий
     * Публичный API для работы с подборками событий
     */

    /**
     * Получение подборок событий
     */
    @GetMapping("/compilations")
    public List<CompilationDto> findAllCompilations(@RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam boolean pinned) {
        return compilationService.findAllCompilations(from, size, pinned);
    }

    /**
     * Получение подборки события по его id
     */
    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        return compilationService.findCompilationById(compId);
    }

    /**
     Admin: Подборки событий
     API для работы с подборками событий
     */

    /**
     * Добавление новой подборки
     */
    @PostMapping("/admin/compilations")
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    /**
     * Удаление подборки
     */
    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    /**
     * Удаление события из подборки
     */
    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public CompilationDto deleteEventFromCompilation(@PathVariable Long compId,
                                                     @PathVariable Long eventId) {
        return compilationService.deleteEventFromCompilation(compId, eventId);
    }

    /**
     * Добавить событие в подборку
     */
    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public CompilationDto addEventToCompilation(@PathVariable Long compId,
                                                @PathVariable Long eventId) {
        return compilationService.addEventToCompilation(compId, eventId);
    }

    /**
     * Открепить подборку на главной странице
     */
    @DeleteMapping("/admin/compilations/{compId}/pin")
    public CompilationDto unpinCompilationMainPage(@PathVariable Long compId) {
        return compilationService.unpinCompilation(compId);
    }

    /**
     * Закрепить подборку на главной странице
     */
    @PatchMapping("/admin/compilations/{compId}/pin")
    public CompilationDto pinCompilationMainPage(@PathVariable Long compId) {
        return compilationService.pinCompilation(compId);
    }
}
