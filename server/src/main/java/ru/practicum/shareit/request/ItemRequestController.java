package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constant.WebConstant.HEADER_X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @PathVariable Long requestId
    ) {
        return itemRequestService.getById(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByAuthor(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestService.getRequestsByAuthor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody NewItemRequestRequest request
    ) {
        return itemRequestService.create(userId, request);
    }
}
