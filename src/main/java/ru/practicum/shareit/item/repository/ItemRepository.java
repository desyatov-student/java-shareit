package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAllByUserId(Long userId);

    Optional<Item> findByUserIdAndItemId(Long userId, Long itemId);

    Item create(Long userId, Item item);

    Item update(Item updatedItem);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

}
