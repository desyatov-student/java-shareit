package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, QuerydslPredicateExecutor<ItemRequest> {
    Collection<ItemRequest> findByAuthor(User user, Sort sort);
}