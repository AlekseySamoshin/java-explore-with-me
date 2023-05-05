package ru.practicum.dtoMapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

@Component
public class CompilationDtoMapper {
    public Compilation mapNewCompilationDtoToCompilation(NewCompilationDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();
    }

    public CompilationDto mapCompilationToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

//    public Compilation mapUpdateCompilationDtoToCompilation(Long compilationId, UpdateCompilationRequest compilationDto) {
//        return Compilation.builder()
//                .id(compilationId)
//                .pinned(compilationDto.getPinned())
//                .title(compilationDto.getTitle())
//                .build();
//    }
}
