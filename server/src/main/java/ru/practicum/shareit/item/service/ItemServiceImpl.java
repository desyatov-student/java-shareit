package ru.practicum.shareit.item.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DateMapper;
import ru.practicum.shareit.utils.Tuple;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final DateMapper dateMapper;

    @Override
    public ItemWithCommentsDto getById(Long itemId) {
        Item item = getItemById(itemId);
        return itemMapper.toDto(item, getCommentsByItems(List.of(item)).getOrDefault(item, List.of()));
    }

    @Override
    public List<ItemWithCommentsDto> getItems(Long userId) {
        checkUserIsExistingById(userId);
        List<Item> items = itemRepository.findByUserId(userId);
        Map<Item, List<Comment>> commentsByItems = getCommentsByItems(items);
        Map<Item, List<Booking>> bookingsByItems = getBookingsByItems(items);

        return items.stream().map(item -> {
            ItemWithCommentsDto itemDto = itemMapper.toDto(item, commentsByItems.getOrDefault(item, List.of()));
            Tuple<String, String> dates = findLastAndNextDates(bookingsByItems.getOrDefault(item, List.of()));
            itemDto.setLastBooking(dates.first);
            itemDto.setNextBooking(dates.second);
            return itemDto;
        }).toList();
    }

    @Transactional
    @Override
    public ItemDto create(Long userId, NewItemRequest request) {
        User user = getUserById(userId);
        ItemRequest itemRequest = null;
        if (request.getRequestId() != null) {
            itemRequest = getItemRequestById(request.getRequestId());
        }
        Item item = itemMapper.toItem(request, user, itemRequest);
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
        BooleanExpression byBookerId = QBooking.booking.booker.id.eq(userId);
        BooleanExpression byItemId = QBooking.booking.item.id.eq(itemId);
        BooleanExpression byEnd = QBooking.booking.end.before(DateMapper.now());
        Predicate predicate = byBookerId.and(byItemId).and(byEnd);
        boolean bookingExists = bookingRepository.exists(predicate);
        if (!bookingExists) {
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

    private Map<Item, List<Comment>> getCommentsByItems(Collection<Item> items) {
       return commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
    }

    private Map<Item, List<Booking>> getBookingsByItems(Collection<Item> items) {
        Sort sort = new QSort(QBooking.booking.start.asc());
        return bookingRepository.findByItemIn(items, sort)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
    }

    private Tuple<String, String> findLastAndNextDates(List<Booking> bookings) {
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
        return new Tuple<>(lastBooking, nextBooking);
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

    private ItemRequest getItemRequestById(Long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }

    private void checkUserIsExistingById(Long userId) {
        userService.getUserById(userId);
    }
}
