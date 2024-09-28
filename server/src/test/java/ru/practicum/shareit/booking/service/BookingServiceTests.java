package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.helpers.TestData;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DateMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@ComponentScan("ru.practicum")
class BookingServiceTests {

    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private DateMapper dateMapper;
    @Autowired
    private BookingService bookingService;

    private final String startDate = "2000-01-01T10:00:00";
    private final String endDate = "2000-01-02T10:00:00";
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {

        long bookerId = 1L;
        long ownerId = 2L;
        long itemId = 3L;
        long bookingId = 4L;

        owner = TestData.createUser();
        owner.setId(ownerId);

        booker = TestData.createUser();
        booker.setId(bookerId);

        item = TestData.createItem(owner);
        item.setId(itemId);

        booking = createBooking(bookingId, booker, item);
    }

    @Test
    void create_returnNewBooking_itemIsAvailableAndBookerHasNotBooked() {

        // Given

        item.setAvailable(true);

        NewBookingRequest request = new NewBookingRequest(
                item.getId(),
                dateMapper.toLocalDateTime(startDate),
                dateMapper.toLocalDateTime(endDate)
        );

        when(bookingRepository.findByBookerIdAndItemId(booker.getId(), item.getId())).thenReturn(Optional.empty());
        when(itemService.getItemById(item.getId())).thenReturn(item);
        when(userService.getUserById(booker.getId())).thenReturn(booker);

        // When
        BookingDto actualBookingDto = bookingService.create(booker.getId(), request);

        // Then

        checkActualBooking(actualBookingDto, item, booker, true, BookingStatus.WAITING);

        Mockito.verify(bookingRepository).save(any());
        Mockito.verify(bookingRepository).findByBookerIdAndItemId(booker.getId(), item.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verify(itemService).getItemById(item.getId());
        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(booker.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void approve_bookingIsApproved_itemIsAvailableAndBookingIsWaiting() {

        // Given

        item.setAvailable(true);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.getUserById(owner.getId())).thenReturn(owner);

        // When
        BookingDto actualBookingDto = bookingService.approve(owner.getId(), booking.getId(), true);

        // Then

        checkActualBooking(actualBookingDto, item, booker, false, BookingStatus.APPROVED);

        Mockito.verify(bookingRepository).save(any());
        Mockito.verify(bookingRepository).findById(booking.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(owner.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getById_returnBooking_UserIsOwner() {
        // Given

        item.setAvailable(false);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.getUserById(owner.getId())).thenReturn(owner);

        // When
        BookingDto actualBookingDto = bookingService.getById(booking.getId(), owner.getId());

        // Then

        checkActualBooking(actualBookingDto, item, booker, false, BookingStatus.APPROVED);

        Mockito.verify(bookingRepository).findById(booking.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(owner.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getById_returnBooking_UserIsBooker() {
        // Given

        item.setAvailable(false);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.getUserById(booker.getId())).thenReturn(booker);

        // When
        BookingDto actualBookingDto = bookingService.getById(booking.getId(), booker.getId());

        // Then

        checkActualBooking(actualBookingDto, item, booker, false, BookingStatus.APPROVED);

        Mockito.verify(bookingRepository).findById(booking.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(booker.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getBookingsByBooker_returnListOfBookings_UserBookedItem() {
        // Given

        item.setAvailable(false);
        booking.setStatus(BookingStatus.APPROVED);

        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(booker.getId());
        Sort sort = new QSort(QBooking.booking.createDate.desc());
        when(bookingRepository.findAll(byBookerId, sort)).thenReturn(List.of(booking));
        when(userService.getUserById(booker.getId())).thenReturn(booker);

        // When
        List<BookingDto> actualBookings = bookingService.getBookingsByBooker(booker.getId(), BookingState.ALL);

        // Then

        checkActualBooking(actualBookings.get(0), item, booker, false, BookingStatus.APPROVED);

        Mockito.verify(bookingRepository).findAll(byBookerId, sort);
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(booker.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getBookingsByOwner_returnListOfBookings_UserIsOwner() {
        // Given

        BooleanExpression byOwnerId = QBooking.booking.item.id.eq(owner.getId());
        Sort sort = new QSort(QBooking.booking.createDate.desc());
        when(bookingRepository.findAll(byOwnerId, sort)).thenReturn(List.of(booking));
        when(userService.getUserById(owner.getId())).thenReturn(owner);

        // When
        List<BookingDto> actualBookings = bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL);

        // Then

        checkActualBooking(actualBookings.get(0), item, booker, true, BookingStatus.WAITING);

        Mockito.verify(bookingRepository).findAll(byOwnerId, sort);
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verifyNoMoreInteractions(itemService);

        Mockito.verify(userService).getUserById(owner.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    private Booking createBooking(long bookingId, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(dateMapper.toInstant(startDate));
        booking.setEnd(dateMapper.toInstant(endDate));
        return booking;
    }

    private void checkActualBooking(BookingDto actualBookingDto, Item item, User booker, boolean available, BookingStatus status) {
        // Item
        assertThat(actualBookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(actualBookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(actualBookingDto.getItem().getDescription()).isEqualTo(item.getDescription());
        assertThat(actualBookingDto.getItem().getAvailable()).isEqualTo(available);

        // User
        assertThat(actualBookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(actualBookingDto.getBooker().getEmail()).isEqualTo(booker.getEmail());
        assertThat(actualBookingDto.getBooker().getName()).isEqualTo(booker.getName());

        // Booking
        assertThat(actualBookingDto.getStart()).isEqualTo(startDate);
        assertThat(actualBookingDto.getEnd()).isEqualTo(endDate);
        assertThat(actualBookingDto.getStatus()).isEqualTo(status.toString());
        assertThat(actualBookingDto.getCreateDate()).isNotBlank();
    }
}