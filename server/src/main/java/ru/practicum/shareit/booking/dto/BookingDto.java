package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class BookingDto {
    private Long id;
    private ItemDto item;
    private UserDto booker;
    private String createDate;
    private String start;
    private String end;
    private String status;
}
