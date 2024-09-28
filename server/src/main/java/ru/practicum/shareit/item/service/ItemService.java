package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemWithCommentsDto getById(Long itemId);

    Item getItemById(Long itemId);

    List<ItemWithCommentsDto> getItems(Long userId);

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest request);

    void remove(Long userId, Long itemId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentRequest request);
}
