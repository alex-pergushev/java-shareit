package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dao.ItemRequestRepository;
import ru.practicum.shareit.requests.RequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    @Autowired
    public ItemRequestServiceImpl(
            UserRepository userRepository, ItemRepository itemRepository, ItemRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto requestDto) {
        validateOwner(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setRequestor(userId);
        return RequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {
        validateOwner(userId);
        List<ItemRequest> requests = requestRepository.findByRequestor(userId);
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemRequestDto> requestDtos = requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        addResponse(requestDtos);
        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        validateOwner(userId);
        checkPageParameters(from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        Page<ItemRequest> pages = requestRepository.findAllByRequestorNot(userId, pageable);
        if (pages.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemRequestDto> itemRequestDtos = pages.map(RequestMapper::toRequestDto).toList();
        addResponse(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        validateOwner(userId);
        ItemRequest request = requestRepository.findById(
                requestId).orElseThrow(() -> new ObjectNotFoundException("Запрос не найден."));
        ItemRequestDto requestDto = RequestMapper.toRequestDto(request);
        addResponse(List.of(requestDto));
        return requestDto;
    }

    private List<ItemRequestDto> addResponse(List<ItemRequestDto> requestDtos) {
        for (ItemRequestDto requestDto : requestDtos) {
            List<Item> items = itemRepository.findByRequestId(requestDto.getId());
            requestDto.setItems(items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        }
        return requestDtos;
    }

    private void validateOwner(long userId) {
        if (userId == 0) {
            log.debug("Не задан владелец: {}", userId);
            throw new ObjectNotFoundException(String.format("Не задан владелец: %d.", userId));
        }
        if (userRepository.findById(userId).isEmpty()) {
            log.debug("Не найден владелец: {}", userId);
            throw new ObjectNotFoundException(String.format("Не найден владелец: %d.", userId));
        }
    }

    private void checkPageParameters(int from, int size) {
        if (size <= 0) {
            log.debug("Количество вещей на странице должно быть больше 0");
            throw new ValidationException("Количество вещей на странице должно быть больше 0");
        }
        if (from < 0) {
            log.debug("Индекс первого элемента должен быть больше 0");
            throw new ValidationException("Индекс первого элемента должен быть больше 0");
        }
    }
}
