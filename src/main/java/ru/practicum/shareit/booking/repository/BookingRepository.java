package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bk " +
            "join b.item as i " +
            "where bk.id=?1 and i.id=?2")
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    Optional<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(Long bookerId, Long itemId, Instant current);

    List<Booking> findByItem_Id(Long itemId);
}