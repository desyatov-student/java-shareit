package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank
    private String name;
    private String url;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
}
