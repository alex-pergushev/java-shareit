package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDtoResponse.builder()
                .created(comment.getCreated())
                .author(comment.getAuthor().getName())
                .text(comment.getText())
                .id(comment.getId())
                .build();
    }

}
