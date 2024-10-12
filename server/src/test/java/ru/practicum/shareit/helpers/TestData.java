package ru.practicum.shareit.helpers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.DateMapper;

import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    public static BookingDto createBookingDto() {
        return new BookingDto(
                1L,
                createItemDto(),
                createUserDto(),
                "2024-01-01 10:00:00.000",
                "2024-01-01 11:00:00.000",
                "2024-01-10 12:00:00.000",
                "WAITING"
        );
    }

    public static Booking createBooking(User booker, Item item) {
        return createBooking(booker, item, LocalDateTime.now().plusDays(1));
    }

    public static Booking createBooking(User booker, Item item, LocalDateTime start) {
        DateMapper dateMapper = new DateMapper();
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(dateMapper.toInstant(start));
        booking.setEnd(dateMapper.toInstant(start.plusDays(1)));
        return booking;
    }

    public static List<BookingDto> createBookings() {
        return List.of(
                new BookingDto(
                        1L,
                        new ItemDto(1L, "BookingDtoName1", "BookingDtoDesc1", true),
                        createUserDto(),
                        "2024-01-01 10:00:00.000",
                        "2024-01-01 11:00:00.000",
                        "2024-01-10 12:00:00.000",
                        "WAITING"
                ),
                new BookingDto(
                        2L,
                        new ItemDto(2L, "BookingDtoName2", "BookingDtoDesc2", true),
                        createUserDto(),
                        "2024-01-01 10:00:00.000",
                        "2024-01-01 11:00:00.000",
                        "2024-01-10 12:00:00.000",
                        "WAITING"
                )
        );
    }

    public static UserDto createUserDto() {
        return new UserDto(
                1L,
                "email@mail.ru",
                "UserDtoName"
        );
    }

    public static User createUser() {
        return createUser("email@mail.ru");
    }

    public static User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("UserName");
        return user;
    }

    public static ItemDto createItemDto() {
        return new ItemDto(
                1L,
                "ItemDtoName",
                "ItemDtoDesc",
                true
        );
    }

    public static Item createItem(User user) {
        Item item = new Item();
        item.setUser(user);
        item.setName("ItemName");
        item.setDescription("ItemDesc");
        item.setAvailable(true);
        return item;
    }
}