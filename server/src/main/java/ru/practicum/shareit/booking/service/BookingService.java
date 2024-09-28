package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DateMapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto create(Long bookerId, NewBookingRequest request) {
        User user = getUserById(bookerId);
        // проверка повторного бронирования
        if (bookingRepository.findByBookerIdAndItemId(bookerId, request.getItemId()).isPresent()) {
            String errorMessage = String.format("Booking for user id=%d is exist", bookerId);
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        Item item = getItemById(request.getItemId());
        // проверка доступности вещи
        if (!item.getAvailable()) {
            String errorMessage = String.format("Item with id=%d is not available", item.getId());
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        Booking booking = bookingMapper.update(new Booking(), user, item, request);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        // проверяем статус текущей брони. Только для WAITING можно выполнить операцию
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            String errorMessage = String.format("Booking with id=%d is %s", userId, booking.getStatus());
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        User user;
        try {
            user = getUserById(userId);
        } catch (Exception e) {
            String errorMessage = String.format("Пользователь с id=%d не найден", userId);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        // проверяем владельца. только владелец может подтвердить бронь
        checkOwner(user.getId(), booking.getItem());
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        booking.getItem().setAvailable(false);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        User user = getUserById(userId);
        // проверяем пользователя. владелец вещи или тот кто забронировал
        checkUserAccess(user.getId(), booking);
        return bookingMapper.toDto(booking);
    }

    public List<BookingDto> getBookingsByBooker(Long bookerId, BookingState state) {
        User booker = getUserById(bookerId);
        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(booker.getId());
        return getBookingsByUserPredicate(byBookerId, state);
    }

    public List<BookingDto> getBookingsByOwner(Long userId, BookingState state) {
        User owner = getUserById(userId);
        BooleanExpression byUserId = QBooking.booking.item.id.eq(owner.getId());
        return getBookingsByUserPredicate(byUserId, state);
    }

    private List<BookingDto> getBookingsByUserPredicate(BooleanExpression userPredicate, BookingState state) {
        Sort sort = new QSort(QBooking.booking.createDate.desc());

        Predicate predicate = switch (state) {
            case ALL -> userPredicate;
            case CURRENT -> {
                Instant now = DateMapper.now();
                BooleanExpression between = Expressions.asDate(now).between(QBooking.booking.start, QBooking.booking.end);
                yield userPredicate.and(between);
            }
            case PAST -> {
                Instant now = DateMapper.now();
                BooleanExpression byEnd = QBooking.booking.end.before(now);
                yield userPredicate.and(byEnd);
            }
            case FUTURE -> {
                Instant now = DateMapper.now();
                BooleanExpression byStart = QBooking.booking.start.after(now);
                yield userPredicate.and(byStart);
            }
            case WAITING -> {
                BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.WAITING);
                yield userPredicate.and(byStatus);
            }
            case REJECTED -> {
                BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.REJECTED);
                yield userPredicate.and(byStatus);
            }
        };

        Iterable<Booking> bookings = bookingRepository.findAll(predicate, sort);
        return StreamSupport.stream(bookings.spliterator(), false)
                .map(bookingMapper::toDto)
                .toList();
    }

    private void checkOwner(Long userId, Item item) {
        if (!item.getUser().getId().equals(userId)) {
            String errorMessage = String.format("User with id=%d is not owner of item with id=%d", userId, item.getId());
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkUserAccess(Long userId, Booking booking) {
        Long ownerId = booking.getItem().getUser().getId();
        Long bookerId = booking.getBooker().getId();
        if (ownerId.equals(userId) || bookerId.equals(userId)) {
            return;
        }
        String errorMessage = String.format("User with id=%d can't get booking with id=%d", userId, booking.getId());
        log.error(errorMessage);
        throw new NotFoundException(errorMessage);
    }

    private User getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            String errorMessage = String.format("Бронирование с id=%d не найдено", bookingId);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    private Item getItemById(Long itemId) {
        return itemService.getItemById(itemId);
    }
}
