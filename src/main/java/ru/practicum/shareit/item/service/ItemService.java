package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    /**
     * Добавление новой вещи
     *
     * @param item   вещь
     * @param userId идентификатор пользователя владельца вещи
     */
    ItemDto add(ItemDto item, long userId);

    /**
     * Редактирование вещи
     *
     * @param item   вещь
     * @param itemId идентификатор вещи
     * @param userId идентификатор пользователя владельца вещи
     */
    ItemDto update(ItemDto item, Long itemId, long userId);

    /**
     * Просмотр вещи по идентификатору
     *
     * @param itemId идентификатор вещи
     * @return вещь
     */
    ItemDto getDto(Long itemId, Long userId);

    Item getItem(long itemId);

    /**
     * Получение владельцем всех его вещей
     *
     * @param ownerId идентификатор владельца
     * @return вещи
     */
    Collection<ItemDto> findAllItemsByOwner(long ownerId);

    /**
     * Поиск вещи арендатором по тексту в названии или описании
     *
     * @param text строка поиска
     * @return найденные вещи
     */
    Collection<ItemDto> searchItems(String text);

    void delete(Long itemId, Long userId);
}
