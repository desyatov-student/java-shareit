package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.NewCommentRequest;
import ru.practicum.shareit.constant.WebConstant;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithCommentsDto> getItems(@RequestHeader(WebConstant.HEADER_X_SHARER_USER_ID) Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsDto getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto create(
            @RequestHeader(WebConstant.HEADER_X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody NewItemRequest request
    ) {
        return itemService.create(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(WebConstant.HEADER_X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        return itemService.update(userId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public void remove(
            @RequestHeader(WebConstant.HEADER_X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId
    ) {
        itemService.remove(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    // Comments

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(WebConstant.HEADER_X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody NewCommentRequest request
    ) {
        return itemService.addComment(userId, itemId, request);
    }
}
