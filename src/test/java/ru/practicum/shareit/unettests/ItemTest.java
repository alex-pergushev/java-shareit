package ru.practicum.shareit.unettests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
public class ItemTest {

    private static User user = User.builder()
            .id(1)
            .name("Alex")
            .email("alex@pergushev.ru")
            .build();
    private static ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Дрель")
            .description("С ударным механизмом")
            .owner(1)
            .available(true)
            .request(0)
            .build();
    private static Item item = Item.builder()
            .id(1)
            .name("Дрель")
            .description("С ударным механизмом")
            .owner(1)
            .isAvailable(true)
            .request(0)
            .build();
    private static Booking dto = Booking.builder()
            .id(1)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(2))
            .item(new Item())
            .booker(new User())
            .status(BookingStatus.APPROVED)
            .build();

    private ItemService itemService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(
                mockItemRepository,
                mockUserRepository,
                mockBookingRepository,
                mockCommentRepository
        );
    }

    @Test
    void createOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto getItem = itemService.create(1, itemDto);
        Assertions.assertEquals(getItem.getName(), itemDto.getName());
    }

    @Test
    void createUserNotFound() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.create(1, itemDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Не найден владелец с идентификатором: 1.");
    }

    @Test
    void updateOk() {

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto getItem = itemService.update(1,1,itemDto);
        Assertions.assertEquals(getItem.getName(), itemDto.getName());
    }

    @Test
    void updateUserNotFound() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.update(1,1, itemDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Не найден владелец с идентификатором: 1.");
    }

    @Test
    void updateItemNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.update(1, 1, itemDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Не найден предмет 1");
    }

    @Test
    void updateEditNotOwner() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.update(2, 1, itemDto)
        );
        Assertions.assertEquals(exception.getMessage(), "Редактирование доступно только для владельца.");
    }

    @Test
    void findAllByIdOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Pageable pageable = PageRequest.of(0, 20);
        Mockito
                .when(mockItemRepository.findIdByOwner(1, pageable))
                .thenReturn(new PageImpl<>(List.of(1L)));
        Mockito
                .when(mockItemRepository.getByIdForResponse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto);
        List<ItemDto> getItems = itemService.findAllById(1, 1, 20);
        Assertions.assertEquals(getItems.size(), 1);
    }

    @Test
    void findAllByIdUserNotFound() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.findAllById(1, 1, 20)
        );
        Assertions.assertEquals(exception.getMessage(), "Не найден владелец с идентификатором: 1.");
    }

    @Test
    void findAllByIdPageParametersFail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.findAllById(1, -1, 20)
        );
        Assertions.assertEquals(
                exception.getMessage(), "Индекс первого элемента должен быть больше 0");

        final ValidationException exceptionAnother = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.findAllById(1, 1, 0)
        );
        Assertions.assertEquals(
                exceptionAnother.getMessage(), "Количество вещей на странице должно быть больше 0");
    }

    @Test
    void searchOk() {
        Pageable pageable = PageRequest.of(0, 20);
        Mockito
                .when(mockItemRepository.search("поиск", pageable))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> getItems = itemService.search("поиск", 1, 20);
        Assertions.assertEquals(getItems.size(), 1);
    }

    @Test
    void searchEmptyText() {
        Pageable pageable = PageRequest.of(0, 20);
        Mockito
                .when(mockItemRepository.search("поиск", pageable))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> getItems = itemService.search("", 1, 20);
        Assertions.assertEquals(getItems.size(), 0);
    }

    @Test
    void searchPageParametersFail() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.search("1", -1, 20)
        );
        Assertions.assertEquals(
                exception.getMessage(), "Индекс первого элемента должен быть больше 0");

        final ValidationException exceptionAnother = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.search("1", 1, 0)
        );
        Assertions.assertEquals(
                exceptionAnother.getMessage(), "Количество вещей на странице должно быть больше 0");
    }

    @Test
    void addCommentOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(dto));
        Mockito
                .when(mockCommentRepository.save(new Comment()))
                .thenReturn(new Comment());
        itemService.addComment(1, 1, new CommentDto("комментарий"));
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class));
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verify(mockUserRepository, Mockito.times(2))
                .findById(Mockito.anyLong());
        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .save(any(Comment.class));
    }

    @Test
    void addCommentNotBooking() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, new CommentDto("комментарий"))
        );
        Assertions.assertEquals(exception.getMessage(), "Пользователь с идентификатором 1 не бронировал вещь.");
    }

    @Test
    void mapperToItem() {
        Item getItem = ItemMapper.toItem(itemDto);
        Assertions.assertEquals(getItem.getName(), itemDto.getName());

        getItem = ItemMapper.toItem(null);
        Assertions.assertNull(getItem);
    }

    @Test
    void mapperToItemDto() {
        ItemDto getItemDto = ItemMapper.toItemDto(item);
        Assertions.assertEquals(getItemDto.getName(), item.getName());

        getItemDto = ItemMapper.toItemDto(null);
        Assertions.assertNull(getItemDto);
    }
}