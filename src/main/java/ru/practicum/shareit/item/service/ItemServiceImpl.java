package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.*;

@Component
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentService commentService;

    @Autowired
    public ItemServiceImpl(UserService userService,
                           ItemRepository itemRepository,
                           CommentService commentService) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.commentService = commentService;
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User owner = userService.getUser(userId);

        validateFieldItem(itemDto);

        Item addItem = ItemMapper.toItem(itemDto);
        addItem.setOwner(owner);

        addItem = itemRepository.saveAndFlush(addItem);

        return ItemMapper.toItemDto(addItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, long userId) {

        Item updateItem = getItem(itemId);

        if (updateItem.getOwner().getId() != userId) {
            throw new AccessForbiddenException("Можно вносить изменения только в свои вещи");
        }
        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        updateItem = itemRepository.saveAndFlush(updateItem);

        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto getDto(Long itemId, Long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(getItem(itemId));
        if (itemDto.getOwner() != null && itemDto.getOwner().getId() == userId) {
            itemDto.setLastBooking(getLastBooking(itemId));
            itemDto.setNextBooking(getNextBooking(itemId));
        }
        itemDto.setComments(commentService.findItemComments(itemId));
        return itemDto;
    }

    @Override
    public Item getItem(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new EntityNotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId));
        }
    }

    @Override
    public Collection<ItemDto> findAllItemsByOwner(long ownerId) {
        List<ItemDto> result = new ArrayList<>();
        ItemDto itemDto = null;
        for (Item item : itemRepository.findItemsByOwnerIdOrderById(ownerId)) {
            itemDto = ItemMapper.toItemDto(item);
            itemDto.setLastBooking(getLastBooking(item.getId()));
            itemDto.setNextBooking(getNextBooking(item.getId()));
            itemDto.setComments(commentService.findItemComments(item.getId()));
            result.add(itemDto);
        }
        return result;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        List<ItemDto> result = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            for (Item item : itemRepository.search(text)) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    @Override
    public void delete(Long itemId, Long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (!item.isPresent()) {
            throw new EntityNotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId));
        }
        if (item.get().getOwner().getId() != userId) {
            throw new AccessForbiddenException("Можно удалять только свои вещи");
        }
        itemRepository.deleteById(itemId);
    }

    private void validateFieldItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Не заполнено поле доступности");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Не заполнено название вещи");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Не заполнено описание вещи");
        }
    }

    private ItemDto.Booking getLastBooking(Long itemId) {
        Object[] objects = itemRepository.findLastBooking(itemId, Date.from(Instant.now())).get(0);
        if (objects[0] != null && objects[1] != null) {
            return new ItemDto.Booking(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()));
        } else {
            return null;
        }
    }

    private ItemDto.Booking getNextBooking(Long itemId) {
        Object[] objects = itemRepository.findNextBooking(itemId, Date.from(Instant.now())).get(0);
        if (objects[0] != null && objects[1] != null) {
            return new ItemDto.Booking(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()));
        } else {
            return null;
        }
    }
}
