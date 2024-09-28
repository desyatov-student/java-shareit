package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@StartBeforeEndDateValid
public class NewBookingRequest {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
