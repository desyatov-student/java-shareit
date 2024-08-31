package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getById(Long itemId) {
        Item item = getItemById(itemId);
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        checkUserById(userId);
        return itemRepository.findAllByUserId(userId).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto create(Long userId, NewItemRequest request) {
        checkUserById(userId);
        Item item = itemMapper.toItem(request);
        item = itemRepository.create(userId, item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest request) {
        Item item = getItemByUserIdAndItemId(userId, itemId);
        item = itemMapper.updateItem(item, request);
        item = itemRepository.update(item);
        return itemMapper.toDto(item);
    }

    @Override
    public void remove(Long userId, Long itemId) {
        checkUserById(userId);
        getItemByUserIdAndItemId(userId, itemId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            String errorMessage = String.format("Элемент id = %d не найден", itemId);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    private Item getItemByUserIdAndItemId(Long userId, Long itemId) {
        return itemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Для пользователя id=%d Элемент id = %d не найден", userId, itemId);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private void checkUserById(Long userId) {
        userService.getById(userId);
    }
}
