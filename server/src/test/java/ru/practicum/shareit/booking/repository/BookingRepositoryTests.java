package ru.practicum.shareit.booking.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.helpers.TestData;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTests {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void findById_returnEntity_EntityWasSaved() {
        // Given
        User booker = userRepository.save(TestData.createUser());
        Item item = itemRepository.save(TestData.createItem(booker));
        Booking booking = bookingRepository.save(TestData.createBooking(booker, item));

        // When
        Optional<Booking> bookingOpt = bookingRepository.findById(booking.getId());

        // Then
        AssertionsForClassTypes.assertThat(bookingOpt)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(booking);
    }

    @Test
    void findByBookerIdAndItemId_returnEntity_EntityWasSaved() {
        // Given
        User booker = userRepository.save(TestData.createUser());
        Item item = itemRepository.save(TestData.createItem(booker));
        Booking booking = bookingRepository.save(TestData.createBooking(booker, item));

        // When
        Optional<Booking> bookingOpt = bookingRepository.findByBookerIdAndItemId(booker.getId(), item.getId());

        // Then
        AssertionsForClassTypes.assertThat(bookingOpt)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(booking);
    }

    @Test
    void findByItemIn_returnListOfEntitySortedByStart_EntitiesWasSaved() {
        // Given
        User owner = userRepository.save(TestData.createUser("email1@mail.ru"));
        User booker1 = userRepository.save(TestData.createUser("email2@mail.ru"));
        User booker2 = userRepository.save(TestData.createUser());
        Item item = itemRepository.save(TestData.createItem(owner));
        Booking booking1 = bookingRepository.save(TestData.createBooking(booker1, item, LocalDateTime.now().plusDays(1)));
        Booking booking2 = bookingRepository.save(TestData.createBooking(booker2, item, LocalDateTime.now().minusDays(1)));
        Sort sort = new QSort(QBooking.booking.start.asc());
        List<Booking> expectedBookings = List.of(booking2, booking1);
        // When
        List<Booking> bookings = bookingRepository.findByItemIn(List.of(item), sort);

        // Then
        for (int i = 0; i < bookings.size(); i++) {
            Booking expectedBooking = expectedBookings.get(i);
            Booking actualBooking = bookings.get(i);
            AssertionsForClassTypes.assertThat(actualBooking)
                    .usingRecursiveComparison()
                    .ignoringActualNullFields()
                    .isEqualTo(expectedBooking);
        }

    }

}