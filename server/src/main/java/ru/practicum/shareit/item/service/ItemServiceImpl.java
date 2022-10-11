package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final String NOT_FOUND = "Не найден предмет ";

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        validateOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        validateOwner(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
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

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        return itemRepository.getByIdForResponse(userId, itemId);
    }

    @Override
    public List<ItemDto> findAllById(long userId, int from, int size) {
        validateOwner(userId);
        checkPageParameters(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.findIdByOwner(userId, pageable).stream()
                .map(id -> itemRepository.getByIdForResponse(userId, id))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        checkPageParameters(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return text.isBlank() ? Collections.emptyList() : itemRepository.search(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + itemId));
        validateOwner(userId);
        List<Booking> booking = bookingRepository
                .findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());
        if (booking == null || booking.size() == 0) {
            log.debug("Пользователь с идентификатором {} не бронировал вещь: {}", userId, itemId);
            throw new ValidationException(String.format(
                    "Пользователь с идентификатором %d не бронировал вещь.", userId));
        }
        Comment comment = Comment.builder()
                .author(userRepository.findById(userId).get())
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
        if (userRepository.findById(userId).isEmpty()) {
            log.debug("Не найден владелец с идентификатором: {}", userId);
            throw new ObjectNotFoundException(String.format("Не найден владелец с идентификатором: %d.", userId));
        }
    }

    private void checkPageParameters(int from, int size) {
        if (size <= 0) {
            log.debug("Количество вещей на странице должно быть больше 0.");
            throw new ValidationException("Количество вещей на странице должно быть больше 0");
        }
        if (from < 0) {
            log.debug("Индекс первого элемента должен быть больше 0.");
            throw new ValidationException("Индекс первого элемента должен быть больше 0");
        }
    }
}
