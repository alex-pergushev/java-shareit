package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    /**
     * Добавление новой вещи
     * @param item вещь
     */
    ItemDto add(ItemDto item, long userId);

    /**
     * Редактирование вещи
     * @param item вещь
     */
    ItemDto update(ItemDto item, Long itemId, long userId);

    /**
     * Просмотр вещи по идентификатору
     * @param id идентификатор вещи
     * @return вещь
     */
    ItemDto get(long id);

    /**
     * Получение владельцем всех его вещей
     * @param ownerId владелиц
     * @return вещи
     */
    Collection<ItemDto> findAllItemsByOwner(long ownerId);

    /**
     * Поиск вещи арендатором по тексту в названии или описании
     * @param text строка поиска
     * @return найденные вещи
     */
    Collection<ItemDto> search(String text);
}
