package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query("select b " +
            "from Booking as b " +
            "join b.booker as bk " +
            "join b.item as i " +
            "where bk.id=?1 and i.id=?2")
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByItemIn(Collection<Item> items, Sort sort);
}