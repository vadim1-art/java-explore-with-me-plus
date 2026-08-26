package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatsClient;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.util.DateUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getPublishedEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable,
            String sort) {

        // Вычисляем size и offset из pageable
        int size = pageable.getPageSize();
        int offset = pageable.getPageNumber() * size;

        // 1. Получаем только ID событий
        List<Long> eventIds = eventRepository.findPublishedEventIdsWithFilters(
                text, categories, paid, start, end, size, offset);

        if (eventIds.isEmpty()) {
            log.info("События по заданным фильтрам не найдены");
            return Collections.emptyList();
        }

        // 2. Загружаем полные события по ID
        List<Event> events = eventRepository.findAllByIdIn(eventIds);

        // 3. Получаем просмотры для этих событий
        Map<Long, Long> viewsMap = getViewsForEvents(eventIds);

        // 4. Маппим в DTO
        List<EventShortDto> dtos = events.stream()
                .map(event -> {
                    int confirmedRequests = (int) requestRepository.countByEventIdAndStatus(
                            event.getId(), RequestStatus.CONFIRMED);
                    Long views = viewsMap.getOrDefault(event.getId(), 0L);
                    return EventMapper.toEventShortDtoWithStats(event, confirmedRequests, views);
                })
                .collect(Collectors.toList());

        if ("VIEWS".equals(sort)) {
            log.info("Сортировка событий по просмотрам");
            dtos.sort(Comparator.comparing(EventShortDto::getViews));
        }

        return dtos;
    }

    @Override
    public Event getPublishedEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с id=" + eventId + " не найдено");
        }

        return event;
    }

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            // Используем DateUtils для форматирования дат
            String start = DateUtils.format(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
            String end = DateUtils.format(LocalDateTime.now());

            // StatsClient принимает String
            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> Long.parseLong(s.getUri().replace("/events/", "")),
                            ViewStatsDto::getHits,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Не удалось получить просмотры для событий: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}