package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dtoMapper.CompilationDtoMapper;
import ru.practicum.dtoMapper.EventDtoMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationDtoMapper compilationDtoMapper;
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        List<Event> events = eventRepository.findEventsByIds(compilationDto.getEvents());
        Compilation compilation = compilationDtoMapper.mapNewCompilationDtoToCompilation(compilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Подборка сохранена с id=" + compilation.getId());
        CompilationDto result = compilationDtoMapper.mapCompilationToDto(compilation);
        result.setEvents(compilation.getEvents().stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList()));
        return result;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось обновить подборку", "Подборка id=" + compId + " не найдена"));
        List<Event> events = eventRepository.findEventsByIds(compilationDto.getEvents());
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(events);
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation = compilationRepository.save(compilation);
        log.info("Подборка id=" + compId + " обновлена");
        CompilationDto result = compilationDtoMapper.mapCompilationToDto(compilation);
        result.setEvents(compilation.getEvents().stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось удалить подборку", "Подборка id=" + compId + " не найдена"));
        compilationRepository.deleteById(compId);
        log.info("Подборка id=" + compId + " удалена");
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAll(pinned, PageRequest.of(from / size, size));
        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            CompilationDto dto = compilationDtoMapper.mapCompilationToDto(compilation);
            dto.setEvents(compilation.getEvents().stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList()));
            compilationDtos.add(dto);
        }
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось получить подборку", "Подборка id=" + compId + " не найдена")
       );
        CompilationDto result = compilationDtoMapper.mapCompilationToDto(compilation);
        result.setEvents(compilation.getEvents().stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList()));
        log.info("Подборка id=" + compId + " найдена");
        return result;
    }
}
