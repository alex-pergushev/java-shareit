package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> getRequests(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);
}
