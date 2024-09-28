package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.validation.NotBlankOrNull;
import ru.practicum.shareit.validation.ValidEmailOrNull;

@Data
public class UpdateUserRequest {
    private Long id;
    @ValidEmailOrNull
    private String email;
    @NotBlankOrNull
    private String name;
}