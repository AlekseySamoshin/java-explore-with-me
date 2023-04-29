package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;

@Service
public class CompilationService {

    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return new CompilationDto();
    }

    public CompilationDto updateCompilation(UpdateCompilationRequest compilationDto) {
        return new CompilationDto();
    }

    public void deleteCompilationById(Long compId) {

    }
}
