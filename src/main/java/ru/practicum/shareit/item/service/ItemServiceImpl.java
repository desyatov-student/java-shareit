package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        checkUserIsExistingById(userId);
        return itemRepository.findByUserId(userId).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ItemDto create(Long userId, NewItemRequest request) {
        User user = getUserById(userId);
        Item item = itemMapper.toItem(request);
        item.setUser(user);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest request) {
        Item item = getItemById(itemId);
        checkUserAccess(userId, item);
        item = itemMapper.updateItem(item, request);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Transactional
    @Override
    public void remove(Long userId, Long itemId) {
        Item item = getItemById(itemId);
        checkUserAccess(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            String errorMessage = String.format("Элемент id = %d не найден", itemId);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    private void checkUserAccess(Long userId, Item item) {
        User user = getUserById(userId);
        if (!item.getUser().getId().equals(user.getId())) {
            String errorMessage = String.format("User with id=%d is not owner of item with id=%d", userId, item.getId());
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    private User getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    private void checkUserIsExistingById(Long userId) {
        userService.getUserById(userId);
    }
}
