package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentView;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentView> findByItem_Id(Long itemId);
}