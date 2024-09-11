package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.NotBlankOrNull;

@Data
public class UpdateItemRequest {
    @NotBlankOrNull
    private String name;
    private String url;
    @NotBlankOrNull
    private String description;
    private Boolean available;
}
