package ru.practicum.subscription.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.user.UserMapper;

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
}