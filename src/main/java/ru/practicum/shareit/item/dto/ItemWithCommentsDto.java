package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
public class ItemWithCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private String lastBooking;
    private String nextBooking;
}
