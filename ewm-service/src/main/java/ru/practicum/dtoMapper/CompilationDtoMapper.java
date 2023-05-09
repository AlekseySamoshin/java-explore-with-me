package ru.practicum.dtoMapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@Component
public class CompilationDtoMapper {
    EventDtoMapper eventDtoMapper = new EventDtoMapper();

    public Compilation mapNewCompilationDtoToCompilation(NewCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .events(events)
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
}
