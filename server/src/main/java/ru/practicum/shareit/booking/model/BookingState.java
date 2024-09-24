package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = String.format("Wrong state: %s", value.toUpperCase());
            throw new ValidationException(message);
        }
    }
}
