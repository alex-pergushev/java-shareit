package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final String NOT_FOUND = "Не найден предмет ";

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        validateOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        validateOwner(userId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
        if (item.getOwner() != userId) {
            log.debug("Редактирование доступно только для владельца: {}", itemId);
            throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }

        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        return itemStorage.getByIdForResponse(userId, itemId);
    }

    @Override
    public List<ItemDto> findAllById(long userId) {
        validateOwner(userId);
        return itemStorage.findIdByOwner(userId).stream().map(id -> itemStorage.getByIdForResponse(userId, id))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return text.isBlank() ? Collections.emptyList() : itemStorage.search(text).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
        validateOwner(userId);
        List<Booking> booking = bookingRepository
                .findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());
        if (booking == null || booking.size() == 0) {
            log.debug("Пользователь с идентификатором {} не бронировал вещь: {}", userId, itemId);
            throw new ValidationException(String.format(
                    "Пользователь с идентификатором %d не бронировал вещь.", userId));
        }
        Comment comment = Comment.builder()
                .author(userStorage.findById(userId).get())
                .text(commentDto.getText())
                .item(item)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toCommentDtoResponse(commentRepository.save(comment));
    }

    private void validateOwner(long userId) {
        if (userId == 0) {
            log.debug("Не задан владелец с идентификатором: {}", userId);
            throw new ObjectNotFoundException(String.format("Не задан владелец с идентификатором: %d.", userId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.debug("Не найден владелец с идентификатором: {}", userId);
            throw new ObjectNotFoundException(String.format("Не найден владелец с идентификатором: %d.", userId));
        }
    }
}
