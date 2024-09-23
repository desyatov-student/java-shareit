package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewIteRequestRequest {

    private Long authorId;
    private String description;

    public static NewIteRequestRequest of(Long authorId, String description) {
        NewIteRequestRequest request = new NewIteRequestRequest();
        request.setAuthorId(authorId);
        request.setDescription(description);
        return request;
    }
}
