package ru.practicum.subscription.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.subscription.dto.NewSubscriptionDto;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.subscription.model.SubscriptionStatus;
import ru.practicum.user.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class SubscriptionMapper {

    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .subscriber(UserMapper.toUserShortDto(subscription.getSubscriber()))
                .publisher(UserMapper.toUserShortDto(subscription.getPublisher()))
                .status(subscription.getStatus())
                .type(subscription.getType())
                .createdAt(subscription.getCreatedAt())
                .build();
    }

    public static Subscription toSubscription(User subscriber, User publisher, NewSubscriptionDto dto) {
        if (dto == null) {
            return null;
        }

        return Subscription.builder()
                .subscriber(subscriber)
                .publisher(publisher)
                .type(dto.getType())
                .status(SubscriptionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}