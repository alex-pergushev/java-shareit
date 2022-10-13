package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestMapper;
import ru.practicum.shareit.requests.dao.ItemRequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class ItemRequestTests {

    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    private static ItemRequestService itemRequestService;
    private static User user;
    private static Item item;
    private static ItemRequest itemRequest;
    private static ItemRequestDto itemRequestDto;
    private static final LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    private void setUp() {
        user = User.builder()
                .id(1)
                .name("Alex")
                .email("alex@pergushev.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("Дрель")
                .description("С ударным механизмом")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Описание")
                .requestor(1)
                .created(created)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("Описание")
                .requestor(1)
                .created(created)
                .build();
        itemRequestService = new ItemRequestServiceImpl(
                mockUserRepository, mockItemRepository, mockItemRequestRepository
        );
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockItemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
    }

    @Test
    void createOk() {
        Mockito
                .when(mockItemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto dto = itemRequestService.create(1L, itemRequestDto);
        Assertions.assertEquals(dto.getId(), itemRequest.getId());
        Assertions.assertEquals(dto.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(dto.getRequestor(), itemRequest.getRequestor());
    }

    @Test
    void createFailUser() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.create(0L, itemRequestDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Не задан владелец: 0.");
    }

    @Test
    void createUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.create(77L, itemRequestDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Не найден владелец: 77.");
    }

    @Test
    void getRequestOk() {
        Mockito
                .when(mockItemRequestRepository.findByRequestor(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> dtos = itemRequestService.getRequests(1L);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void getRequestsEmpty() {
        Mockito
                .when(mockItemRequestRepository.findByRequestor(Mockito.anyLong()))
                .thenReturn(Collections.emptyList());
        List<ItemRequestDto> dtos = itemRequestService.getRequests(1L);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void getAllOk() {
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorNot(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        List<ItemRequestDto> dtos = itemRequestService.getAll(1L, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void getAllEmpty() {
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorNot(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());
        List<ItemRequestDto> dtos = itemRequestService.getAll(1L, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void getAllFailIndexParametersPage() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAll(1L, -1, 10)
        );
        Assertions.assertEquals(exception.getMessage(),
                "Индекс первого элемента должен быть больше 0");
    }

    @Test
    void getAllFailSizeParametersPage() {
        final ValidationException exceptionAnother = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAll(1L, 1, 0)
        );
        Assertions.assertEquals(exceptionAnother.getMessage(),
                "Количество вещей на странице должно быть больше 0");
    }

    @Test
    void mapperToRequestDto() {
        ItemRequestDto getRequestDto = RequestMapper.toRequestDto(itemRequest);
        Assertions.assertEquals(getRequestDto.getId(), itemRequest.getId());
    }

    @Test
    void mapperToRequestDtoNull() {
        ItemRequestDto getRequestDto = RequestMapper.toRequestDto(null);
        Assertions.assertNull(getRequestDto);
    }
}
