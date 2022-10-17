package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item != null) {
            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description((item.getDescription()))
                    .available(item.isAvailable())
                    .owner(item.getOwner())
                    .request(item.getRequest())
                    .build();
        } else {
            return null;
        }
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            return Item.builder()
                    .id(itemDto.getId())
                    .name(itemDto.getName())
                    .description((itemDto.getDescription()))
                    .owner(itemDto.getOwner())
                    .isAvailable(itemDto.getAvailable())
                    .request(itemDto.getRequest())
                    .build();
        } else {
            return null;
        }
    }
}
