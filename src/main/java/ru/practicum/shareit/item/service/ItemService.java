package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto getByUserIdAndItemId(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest request);

    void remove(Long userId, Long itemId);

    List<ItemDto> search(String text);
}
