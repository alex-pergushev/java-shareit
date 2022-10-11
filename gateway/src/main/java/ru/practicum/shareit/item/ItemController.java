package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemDto item) {
        log.info("Creating item {}, userId={}", item, userId);
        if (userId == 0) {
            log.debug("Не задан владелец.");
            throw new ObjectNotFoundException("Не задан владелец.");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            log.info("Не указано наименование вещи для аренды");
            throw  new ValidationException("Не указано наименование вещи для аренды");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.info("Не указано описание вещи для аренды");
            throw  new ValidationException("Не указано описание вещи для аренды");
        }
        if (item.getAvailable() == null) {
            log.info("Не указана доступность вещи для аренды");
            throw  new ValidationException("Не указана доступность вещи для аренды");
        }
        return itemClient.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemId") long itemId,
                                         @Valid @RequestBody ItemDto itemDto) {
        if (userId == 0) {
            log.debug("Не задан владелец.");
            throw new ObjectNotFoundException("Не задан владелец.");
        }
        log.info("Updating item {}, userId={}", itemDto, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("itemId") long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                              @Positive @RequestParam(required = false, defaultValue = "20") int size) {
        if (userId == 0) {
            log.debug("Не задан владелец.");
            throw new ObjectNotFoundException("Не задан владелец.");
        }
        log.info("Get items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                         @Positive @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Search items by text = {}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Add comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
