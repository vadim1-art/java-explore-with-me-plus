package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.client.StatsClient;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicEventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {

        // Отправляем статистику
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        // Сортировка
        Sort sortBy = Sort.unsorted();
        if ("EVENT_DATE".equals(sort)) {
            sortBy = Sort.by("eventDate").ascending();
        }

        Pageable pageable = PageRequest.of(from / size, size, sortBy);

        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(100);

        return eventService.getPublishedEvents(text, categories, paid, start, end, pageable);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(
            @PathVariable Long id,
            HttpServletRequest request) {

        // Отправляем статистику
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/" + id)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Event event = eventService.getPublishedEventById(id);

        return EventMapper.toEventFullDto(event);
    }
}