package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;

public interface CommentService {

    CommentDto add(CommentDto commentDto, Long itemId, Long userId);

    Collection<CommentDto> findItemComments(Long itemId);
}
