package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private UserService userService;
    private final Map<Long, Item> items = new HashMap<>();

    @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto add(ItemDto item, long userId) {
        User user = userService.get(userId);

        validateFieldItem(item);

        Item addItem = ItemMapper.toItem(item);
        addItem.setOwner(user);
        items.put(addItem.getId(), addItem);
        return item;
    }

    @Override
    public ItemDto update(ItemDto item, Long itemId, long userId) {

        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException(String.format("Вещь с идентификатором %d не существует", itemId));
        }

        Item updateItem = items.get(itemId);

        if (updateItem.getOwner().getId() != userId) {
            throw new AccessForbiddenException("Можно вносить изменения только в свои вещи");
        }
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto get(long id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public Collection<ItemDto> findAllItemsByOwner(long ownerId) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == ownerId) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        List<ItemDto> result = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            for (Item item : items.values()) {
                if (item.isAvailable() && (item.getName() != null &&
                        item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase()))) {
                    result.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return result;
    }

    private void validateFieldItem(ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле доступности");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Не заполнено название вещи");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Не заполнено описание вещи");
        }
    }
}
