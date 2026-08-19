package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 1. Для Private API: Получить все события, созданные конкретным пользователем (с пагинацией)
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    // 2. Для Private API: Найти конкретное событие по ID и проверить, что оно принадлежит автору
    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    // 3. Для Admin API (Справочно): Проверить, привязаны ли события к категории
    boolean existsByCategoryId(Long categoryId);

    // 4. Для Compilations (Справочно): Получить список событий по списку их ID
    List<Event> findAllByIdIn(List<Long> ids);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (e.eventDate >= :start AND e.eventDate <= :end) " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "ORDER BY e.eventDate ASC")
    List<Event> findPublishedEventsWithFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
}