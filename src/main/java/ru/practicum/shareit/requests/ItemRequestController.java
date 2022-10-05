package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    /**
    * Добавить новый запрос вещи.
    * Эндпоинт — POST /requests
    * Основная часть запроса — текст запроса, где пользователь описывает,
    * какая именно вещь ему нужна. */
    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    /**
     * Получить список своих запросов вместе с данными об ответах на них.
     * Эндпоинт — GET /requests
     * Для каждого запроса должны указываться описание, дата и время создания
     * и список ответов в формате: id вещи, название, id владельца.
     */
    @GetMapping
    public List<ItemRequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    /**
     * Получить список запросов, созданных другими пользователями.
     * Эндпоинт — GET /requests/all?from={from}&size={size}
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
     * на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым.
     * Результаты должны возвращаться постранично. Для этого нужно передать два параметра:
     * from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
    */
    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(required = false, defaultValue = "0") int from,
                                       @RequestParam(required = false, defaultValue = "20") int size) {
        return requestService.getAll(userId, from, size);
    }

    /**
     * Получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
     * что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     * Эндпоинт — GET /requests/{requestId}
     */
    @GetMapping("{requestId}")
    public ItemRequestDto getAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("requestId") long requestId) {
        return requestService.getById(userId, requestId);
    }
}
