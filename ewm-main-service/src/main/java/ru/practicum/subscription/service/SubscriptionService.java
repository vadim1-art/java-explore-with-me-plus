package ru.practicum.subscription.service;

import ru.practicum.event.dto.EventShortDto;
import ru.practicum.subscription.dto.NewSubscriptionDto;
import ru.practicum.subscription.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    // Подписаться на пользователя
    SubscriptionDto subscribe(Long subscriberId, NewSubscriptionDto newSubscriptionDto);

    // Отписаться
    void unsubscribe(Long subscriberId, Long publisherId);

    // Получить все подписки пользователя
    List<SubscriptionDto> getSubscriptions(Long userId, int from, int size);

    // Получить всех подписчиков пользователя
    List<SubscriptionDto> getSubscribers(Long userId, int from, int size);

    // Получить статус подписки
    SubscriptionDto getSubscription(Long subscriberId, Long publisherId);

    // Получить ленту событий по подпискам
    List<EventShortDto> getSubscriptionEvents(Long userId, int from, int size);
}
