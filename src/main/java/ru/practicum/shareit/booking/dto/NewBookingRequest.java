package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class NewBookingRequest {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
