package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constant.WebConstant.HEADER_X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @PathVariable Long bookingId,
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId
    ) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByBooker(userId, BookingState.from(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByOwner(userId, BookingState.from(state));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto create(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody NewBookingRequest request
    ) {
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingService.approve(userId, bookingId, approved);
    }
}
