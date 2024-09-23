package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Data
@ToString
@EqualsAndHashCode
public class ItemRequestDto {
    private Long id;
    private String authorName;
    private Collection<ItemDto> items;
    private String description;
    private String created;
}