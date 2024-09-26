package ru.practicum.shareit.helpers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class TestData {
    public static BookingDto createBooking() {
        return new BookingDto(
                1L,
                createItem(),
                createUserDto(),
                "2024-01-01 10:00:00.000",
                "2024-01-01 11:00:00.000",
                "2024-01-10 12:00:00.000",
                "WAITING"
        );
    }

    public static UserDto createUserDto() {
        return new UserDto(
                1L,
                "email@mail.ru",
                "name"
        );
    }

    public static User createUser() {
        User user = new User();
        user.setEmail("email@mail.ru");
        user.setName("name");
        return user;
    }

    public static ItemDto createItem() {
        return new ItemDto(
                1L,
                "name",
                "desc",
                true
        );
    }
}