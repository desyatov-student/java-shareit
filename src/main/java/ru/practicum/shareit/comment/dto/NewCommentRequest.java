package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static ru.practicum.shareit.constant.WebConstant.COMMENT_MAX_SIZE;

@Data
public class NewCommentRequest {
    @NotBlank
    @Size(max = COMMENT_MAX_SIZE)
    private String text;
}