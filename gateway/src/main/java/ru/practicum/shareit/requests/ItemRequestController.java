package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllById(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests by userId={}", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") long requestId) {
        log.info("Get requests by userId={} and by requestId={}", userId, requestId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                         @Positive @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Get requests userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAll(userId, from, size);
    }

}
