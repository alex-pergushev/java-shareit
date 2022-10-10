package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

public class RequestMapper {
    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setCreated(itemRequest.getCreated());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setRequestor(itemRequest.getRequestor());
        return requestDto;
    }
}
