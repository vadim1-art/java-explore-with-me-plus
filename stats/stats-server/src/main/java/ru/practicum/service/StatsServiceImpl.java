package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
        statsRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.getStatsWithUniqueIp(start, end, uris);
        } else {
            return statsRepository.getStatsAll(start, end, uris);
        }
    }
}