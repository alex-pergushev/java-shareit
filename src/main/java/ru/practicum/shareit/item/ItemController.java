package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * обработка запросов по вещам
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    /**
     * Добавление новой вещи. На вход поступает объект ItemDto.
     * userId в заголовке X-Sharer-User-Id — это идентификатор пользователя, который добавляет вещь.
     * этот пользователь — владелец вещи.
     */

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto item,
                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(item, userId);
    }

    /**
     * Редактирование вещи.
     * Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     */

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@Valid @RequestBody ItemDto item,
                             @PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(item, itemId, userId);
    }

    /**
     * Просмотр информации о конкретной вещи по её идентификатору.
     * Информацию о вещи может просмотреть любой пользователь.
     */

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable long itemId) {
        return itemService.get(itemId);
    }

    /**
     * Просмотр владельцем списка всех его вещей
     * с указанием названия и описания для каждой.
     */
    @GetMapping
    public Collection<ItemDto> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.findAllItemsByOwner(ownerId);
    }

    /**
     * Поиск вещи потенциальным арендатором.
     * Пользователь передаёт в строке запроса текст, и система ищет вещи,
     * содержащие этот текст в названии или описании.
     * Поиск возвращает только доступные для аренды вещи.
     */

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.search(text);
    }
}
