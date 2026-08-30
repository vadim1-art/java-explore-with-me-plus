package ru.practicum.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.subscription.model.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Проверить, подписан ли пользователь
    boolean existsBySubscriberIdAndPublisherId(Long subscriberId, Long publisherId);

    // Найти подписку по подписчику и издателю
    Optional<Subscription> findBySubscriberIdAndPublisherId(Long subscriberId, Long publisherId);

    // Получить всех подписчиков пользователя
    Page<Subscription> findAllByPublisherId(Long publisherId, Pageable pageable);

    // Получить все подписки пользователя
    Page<Subscription> findAllBySubscriberId(Long subscriberId, Pageable pageable);

    // Получить активные подписки пользователя
    @Query("SELECT s FROM Subscription s WHERE s.subscriber.id = :userId AND s.status = :status")
    List<Subscription> findAllBySubscriberIdAndStatus(@Param("userId") Long userId,
                                                      @Param("status") SubscriptionStatus status);

    // Получить активных подписчиков пользователя
    @Query("SELECT s FROM Subscription s WHERE s.publisher.id = :userId AND s.status = :status")
    List<Subscription> findAllByPublisherIdAndStatus(@Param("userId") Long userId,
                                                     @Param("status") SubscriptionStatus status);
}
