package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    /**
     * Добавление новой вещи
     *
     * @param userId идентификатор пользователя владельца вещи
     * @param item   вещь
     */
    ItemDto create(long userId, ItemDto item);

    /**
     * Редактирование вещи
     *
     * @param userId  идентификатор пользователя владельца вещи
     * @param itemId  идентификатор вещи
     * @param itemDto вещь
     */
    ItemDto update(long userId, long itemId, ItemDto itemDto);

    /**
     * Просмотр вещи по идентификатору
     *
     * @param userId идентификатор пользователя владельца вещи
     * @param itemId идентификатор вещи
     * @return вещь
     */
    ItemDto findById(long userId, long itemId);

    /**
     * Получение владельцем всех его вещей
     *
     * @param userId идентификатор владельца
     * @return вещи
     */
    List<ItemDto> findAllById(long userId);

    /**
     * Поиск вещи арендатором по тексту в названии или описании
     *
     * @param text строка поиска
     * @return найденные вещи
     */
    List<ItemDto> search(String text);

    CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto);
}
