package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.NotBlankOrNull;

@Data
public class UpdateItemRequest {
    @NotBlankOrNull
    private String name;
    @NotBlankOrNull
    private String description;
    private Boolean available;
}
