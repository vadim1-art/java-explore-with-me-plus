package ru.practicum.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.subscription.SubscriptionRepository;
import ru.practicum.subscription.dto.NewSubscriptionDto;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.dto.UpdateSubscriptionDto;
import ru.practicum.subscription.mapper.SubscriptionMapper;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.subscription.model.SubscriptionStatus;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public SubscriptionDto subscribe(Long subscriberId, NewSubscriptionDto newSubscriptionDto) {
        // Нельзя подписаться на самого себя
        if (subscriberId.equals(newSubscriptionDto.getPublisherId())) {
            throw new ConflictException("Cannot subscribe to yourself");
        }

        // Проверяем, существует ли подписчик
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException("User with id=" + subscriberId + " not found"));

        // Проверяем, существует ли издатель
        User publisher = userRepository.findById(newSubscriptionDto.getPublisherId())
                .orElseThrow(() -> new NotFoundException("User with id=" + newSubscriptionDto.getPublisherId() + " not found"));

        // Проверяем, не подписан ли уже
        if (subscriptionRepository.existsBySubscriberIdAndPublisherId(subscriberId, newSubscriptionDto.getPublisherId())) {
            throw new ConflictException("Already subscribed to user with id=" + newSubscriptionDto.getPublisherId());
        }

        Subscription subscription = SubscriptionMapper.toSubscription(subscriber, publisher, newSubscriptionDto);

        subscription = subscriptionRepository.save(subscription);
        log.info("User {} subscribed to user {}", subscriberId, newSubscriptionDto.getPublisherId());

        return SubscriptionMapper.toSubscriptionDto(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long subscriberId, Long publisherId) {
        Subscription subscription = subscriptionRepository
                .findBySubscriberIdAndPublisherId(subscriberId, publisherId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        subscriptionRepository.delete(subscription);
        log.info("User {} unsubscribed from user {}", subscriberId, publisherId);
    }

    @Override
    @Transactional
    public SubscriptionDto updateSubscription(Long subscriberId, Long publisherId, UpdateSubscriptionDto updateDto) {
        Subscription subscription = subscriptionRepository
                .findBySubscriberIdAndPublisherId(subscriberId, publisherId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        // Подписчик может менять только тип уведомлений/подписки
        if (updateDto.getType() != null) {
            subscription.setType(updateDto.getType());
        }


        Subscription updated = subscriptionRepository.save(subscription);
        log.info("Subscription updated: subscriber={}, publisher={}", subscriberId, publisherId);

        return SubscriptionMapper.toSubscriptionDto(updated);
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return subscriptionRepository.findAllBySubscriberId(userId, pageable)
                .stream()
                .map(SubscriptionMapper::toSubscriptionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDto> getSubscribers(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return subscriptionRepository.findAllByPublisherId(userId, pageable)
                .stream()
                .map(SubscriptionMapper::toSubscriptionDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto getSubscription(Long subscriberId, Long publisherId) {
        Subscription subscription = subscriptionRepository
                .findBySubscriberIdAndPublisherId(subscriberId, publisherId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));

        return SubscriptionMapper.toSubscriptionDto(subscription);
    }

    @Override
    public List<EventShortDto> getSubscriptionEvents(Long userId, int from, int size) {
        // 1. Проверяем, существует ли пользователь
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        // 2. Настраиваем пагинацию и сортировку (сначала ближайшие события)
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));

        // 3. Достаем события и мапим их в DTO
        return eventRepository.findEventsBySubscription(userId, EventState.PUBLISHED, pageable)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}
