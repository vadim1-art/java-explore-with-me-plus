package ru.practicum.compilation.service;

import ru.practicum.admin.dto.NewCompilationDto;
import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;

public interface AdminCompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest);
}
