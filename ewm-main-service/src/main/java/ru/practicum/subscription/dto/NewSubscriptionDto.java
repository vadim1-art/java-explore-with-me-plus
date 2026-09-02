package ru.practicum.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewSubscriptionDto {

    @NotNull(message = "Publisher ID cannot be null")
    private Long publisherId;
}
