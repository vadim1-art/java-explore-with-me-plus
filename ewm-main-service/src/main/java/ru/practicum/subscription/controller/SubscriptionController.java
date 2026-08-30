package ru.practicum.subscription.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.subscription.dto.NewSubscriptionDto;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.dto.UpdateSubscriptionDto;
import ru.practicum.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 1. Подписаться на пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(
            @PathVariable Long userId,
            @Valid @RequestBody NewSubscriptionDto newSubscriptionDto) {
        return subscriptionService.subscribe(userId, newSubscriptionDto);
    }

    // 2. Отписаться
    @DeleteMapping("/{publisherId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(
            @PathVariable Long userId,
            @PathVariable Long publisherId) {
        subscriptionService.unsubscribe(userId, publisherId);
    }

    // 3. Обновить подписку
    @PatchMapping("/{publisherId}")
    public SubscriptionDto updateSubscription(
            @PathVariable Long userId,
            @PathVariable Long publisherId,
            @Valid @RequestBody UpdateSubscriptionDto updateDto) {
        return subscriptionService.updateSubscription(userId, publisherId, updateDto);
    }

    // 4. Получить все подписки пользователя
    @GetMapping
    public List<SubscriptionDto> getSubscriptions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return subscriptionService.getSubscriptions(userId, from, size);
    }

    // 5. Получить всех подписчиков пользователя
    @GetMapping("/subscribers")
    public List<SubscriptionDto> getSubscribers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return subscriptionService.getSubscribers(userId, from, size);
    }

    // 6. Получить статус подписки
    @GetMapping("/{publisherId}/status")
    public SubscriptionDto getSubscriptionStatus(
            @PathVariable Long userId,
            @PathVariable Long publisherId) {
        return subscriptionService.getSubscription(userId, publisherId);
    }
}
