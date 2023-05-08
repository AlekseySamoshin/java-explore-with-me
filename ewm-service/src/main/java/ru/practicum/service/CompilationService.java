package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dtoMapper.CompilationDtoMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationService {
    CompilationRepository compilationRepository;
    CompilationDtoMapper compilationDtoMapper;

    @Autowired
    public CompilationService(CompilationRepository compilationRepository, CompilationDtoMapper compilationDtoMapper) {
        this.compilationRepository = compilationRepository;
        this.compilationDtoMapper = compilationDtoMapper;
    }

    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.save(compilationDtoMapper.mapNewCompilationDtoToCompilation(compilationDto));
        log.info("Подборка сохранена с id=" + compilation.getId());
        return compilationDtoMapper.mapCompilationToDto(compilation);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось обновить подборку", "Подборка id=" + compId + " не найдена"));
        if(compilationDto.getEvents() != null) {
            compilation.setEvents(compilationDto.getEvents());
        }
        if(compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if(compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        log.info("Подборка id=" + compId + " обновлена");
        return compilationDtoMapper.mapCompilationToDto(compilationRepository.save(compilation));
    }

    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось удалить подборку", "Подборка id=" + compId + " не найдена"));
        compilationRepository.deleteById(compId);
        log.info("Подборка id=" + compId + " удалена");
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        return compilationRepository.findAll(pinned, PageRequest.of(from/size, size)).stream()
                .map(compilationDtoMapper::mapCompilationToDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не удалось получить подборку", "Подборка id=" + compId + " не найдена")
        );
        log.info("Подборка id=" + compId + " найдена");
        return compilationDtoMapper.mapCompilationToDto(compilation);
    }
}
