package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.Collections;
import java.util.List;

@Service
public class CompilationService {

    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return null;
    }

    public CompilationDto updateCompilation(UpdateCompilationRequest compilationDto) {
        return new null;
    }

    public void deleteCompilationById(Long compId) {

    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    public CompilationDto getCompilationById(Long compId) {
        return null;
    }
}
