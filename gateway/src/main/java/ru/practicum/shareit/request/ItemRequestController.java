package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import static ru.practicum.shareit.constant.WebConstant.HEADER_X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @PathVariable Long requestId
    ) {
        return itemRequestClient.getById(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByAuthor(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId
    ) {
        return itemRequestClient.getRequestsByAuthor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return itemRequestClient.getAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody NewItemRequestRequest request
    ) {
        return itemRequestClient.create(userId, request);
    }
}
