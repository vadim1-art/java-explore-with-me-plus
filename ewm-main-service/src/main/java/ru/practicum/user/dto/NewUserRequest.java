package ru.practicum.user.dto;

import lombok.Data;

@Data
public class NewUserRequest {
    private String name;
    private String email;
}
