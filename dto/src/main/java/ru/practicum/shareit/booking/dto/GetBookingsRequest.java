package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GetBookingsRequest {
    private long userId;
    private BookingState state;
    private Integer from;
    private Integer size;

    public Map<String, Object> buildParameters() {
        return Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
    }
}
