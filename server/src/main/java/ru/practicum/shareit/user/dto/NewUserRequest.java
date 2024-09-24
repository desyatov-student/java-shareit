package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.ValidEmailAndNotBlank;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @ValidEmailAndNotBlank
    private String email;
    @NotBlank
    private String name;
}
