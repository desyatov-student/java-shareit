package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentRequest;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DateMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final DateMapper dateMapper;

    @Override
    public ItemWithCommentsDto getById(Long itemId) {
        Item item = getItemById(itemId);
        return itemMapper.toDto(item, getCommentsByItemId(itemId));
    }

    @Override
    public List<ItemWithCommentsDto> getItems(Long userId) {
        checkUserIsExistingById(userId);
        return itemRepository.findByUserId(userId).stream()
                .map(item -> {
                    ItemWithCommentsDto itemDto = itemMapper.toDto(item, getCommentsByItemId(item.getId()));
                    updateLastAndNextDates(itemDto);
                    return itemDto;
                })
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

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest request) {
        User author = getUserById(userId);
        Item item = getItemById(itemId);
        Optional<Booking> bookingOpt = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId, DateMapper.now());
        if (bookingOpt.isEmpty()) {
            String errorMessage = String.format("User with id=%d has not booked item with id=%d", userId, itemId);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            String errorMessage = String.format("Элемент id = %d не найден", itemId);
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    private List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItem_Id(itemId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    private void updateLastAndNextDates(ItemWithCommentsDto itemDto) {
        Sort sort = new QSort(QBooking.booking.start.asc());
        List<Booking> bookings = bookingRepository.findByItem_Id(itemDto.getId(), sort);
        Instant now = DateMapper.now();
        String lastBooking = null;
        String nextBooking = null;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(now)) {
                lastBooking = dateMapper.toString(booking.getEnd());
            }
            if (now.isBefore(booking.getStart())) {
                nextBooking = dateMapper.toString(booking.getStart());
                break;
            }
        }
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
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
