package ru.practicum.shareit.comment.model;

import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public interface CommentView {
    Long getId();

    String getText();

    @Value("#{target.author.name}")
    String getAuthorName();

    Instant getCreated();
}
