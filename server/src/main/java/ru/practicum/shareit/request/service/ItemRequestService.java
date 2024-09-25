package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    public ItemRequestDto create(Long authorId, NewItemRequestRequest request) {
        User user = getUserById(authorId);
        ItemRequest itemRequest = itemRequestMapper.update(new ItemRequest(), user, request);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toDto(itemRequest);
    }

    public ItemRequestDto getById(Long requestId) {
        ItemRequest itemRequest = getItemRequest(requestId);
        return itemRequestMapper.toDto(itemRequest);
    }

    public ItemRequest getItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> {
            String errorMessage = String.format("Запрос вещи с id=%d не найдено", requestId);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    public List<ItemRequestDto> getRequestsByAuthor(Long authorId) {
        checkUserIsExistingById(authorId);
        Sort sort = new QSort(QItemRequest.itemRequest.created.asc());
        return itemRequestRepository.findByAuthor_Id(authorId, sort).stream()
                .map(itemRequestMapper::toDto)
                .toList();
    }

    public List<ItemRequestDto> getAll() {
        Sort sort = new QSort(QItemRequest.itemRequest.created.asc());
        return itemRequestRepository.findAll(sort).stream()
                .map(itemRequestMapper::toDto)
                .toList();
    }

    private User getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    private void checkUserIsExistingById(Long userId) {
        userService.getUserById(userId);
    }
}
