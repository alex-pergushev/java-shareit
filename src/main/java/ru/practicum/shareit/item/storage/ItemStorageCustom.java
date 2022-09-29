package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemStorageCustom {
    ItemDto getByIdForResponse(long userId, long id);
}
