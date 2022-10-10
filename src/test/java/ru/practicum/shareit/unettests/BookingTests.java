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
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class BookingTests {

    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(3);
    private static UserDto userDto;
    private static UserDto userBooker;
    private static Item item;
    private static Booking booking;
    private static BookingDto bookingDto;
    private BookingService bookingService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private BookingRepository mockBookingRepository;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1)
                .name("Alex")
                .email("alex@pergushev.ru")
                .build();
        userBooker = UserDto.builder()
                .id(2)
                .name("Kirill")
                .email("kirill@pergushev.ru")
                .build();
        item = Item.builder()
                .id(1)
                .name("Дрель")
                .description("С ударным механизмом")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();
        booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(UserMapper.toUser(userBooker))
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(1)
                .booker(2)
                .status(BookingStatus.WAITING)
                .build();
        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemRepository, mockUserService);
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booking));
    }

    @Test
    void createOk() {
        Mockito
                .when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto dto = bookingService.create(2L, bookingDto);
        Assertions.assertEquals(dto.getId(), booking.getId());
        Assertions.assertEquals(dto.getBooker(), booking.getBooker().getId());
        Assertions.assertEquals(dto.getItemId(), booking.getItem().getId());
        Assertions.assertEquals(dto.getStart(), booking.getStart());
        Assertions.assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    void createNotAvailable() {
        item.setAvailable(false);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(2L, bookingDto));
        Assertions.assertEquals(exception.getMessage(), "Вещь не доступна для бронирования.");
    }

    @Test
    void createFailDate() {
        bookingDto.setEnd(start.minusDays(1));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(2L, bookingDto));
        Assertions.assertEquals(exception.getMessage(),
                "Время окончания бронирования должно быть позже времени начала бронирования.");
    }

    @Test
    void createOwnerIsBooking() {
        long userId = 1;
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.create(userId, bookingDto));
        Assertions.assertEquals(exception.getMessage(),
                String.format("Владелец с идентификатором: %d не может забронировать свою вещь.", userId));
    }

    @Test
    void updateOk() {
        Mockito
                .when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingRequestDto dto = bookingService.update(1L, true, 1L);
        Assertions.assertEquals(dto.getId(), booking.getId());
    }

    @Test
    void updateNotOwner() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.update(2L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Редактирование доступно только для владельца.");
    }

    @Test
    void updateRepeatConfirmation() {
        booking.setStatus(BookingStatus.APPROVED);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(1L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Бронирование уже подтверждено.");
    }

    @Test
    void updateNotFoundBooking() {
        Mockito
                .when((mockBookingRepository.findById(Mockito.anyLong())))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.update(1L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Бронирование не найдено: 1");
    }

    @Test
    void findByIdOk() {
        BookingRequestDto dto = bookingService.findById(1L, 1L);
        Assertions.assertEquals(dto.getId(), booking.getId());
    }

    @Test
    void findByIdNoAccess() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.findById(3L, 1L));
        Assertions.assertEquals(exception.getMessage(),
                "Просмотр бронирования доступен для владельца вещи или автора бронирования.");
    }

    @Test
    void findAllByIdAll() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBookerId(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.ALL, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdWAITING() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndStatus(2L, BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.WAITING, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void findAllByIdREJECTED() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndStatus(2L, BookingStatus.REJECTED, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.REJECTED, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void findAllByIdPAST() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndEndBefore(
                        Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.PAST, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdFUTURE() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndStartAfter(
                        Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.FUTURE, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdCURRENT() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.CURRENT, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerAll() {
        Mockito
                .when(mockBookingRepository.findAllByItemIdIn(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.ALL, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerWAITING() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItemIdInAndStatus(1L, BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.WAITING, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerREJECTED() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItemIdInAndStatus(1L, BookingStatus.REJECTED, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.REJECTED, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerPAST() {
        Mockito
                .when(mockBookingRepository.findAllByItemIdInAndEndBefore(
                        Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.PAST, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerFUTURE() {
        Mockito
                .when(mockBookingRepository.findAllByItemIdInAndStartAfter(
                        Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.FUTURE, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerCURRENT() {
        Mockito
                .when(mockBookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.CURRENT, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void getAllFailIndexParametersPage() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwner(1L, BookingState.ALL, -1, 10)
        );
        Assertions.assertEquals(exception.getMessage(), "Индекс первого элемента должен быть больше 0");
    }

    @Test
    void getAllFailSizeParametersPage() {
        final ValidationException exception2 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwner(1L, BookingState.ALL, 1, 0)
        );
        Assertions.assertEquals(exception2.getMessage(), "Количество вещей на странице должно быть больше 0");
    }

    @Test
    void mapperToBooking() {
        Booking getBooking = BookingMapper.toBooking(bookingDto);
        Assertions.assertEquals(getBooking.getId(), bookingDto.getId());
    }

    @Test
    void mapperToBookingNull() {
        Booking getBooking = BookingMapper.toBooking(null);
        Assertions.assertNull(getBooking);
    }

    @Test
    void mapperToBookingDto() {
        BookingDto getBookingDto = BookingMapper.toBookingDto(booking);
        Assertions.assertEquals(getBookingDto.getId(), booking.getId());
    }

    @Test
    void mapperToBookingDtoNull() {
        BookingDto getBookingDto = BookingMapper.toBookingDto(null);
        Assertions.assertNull(getBookingDto);
    }

    @Test
    void mapperToBookingDtoWithBookerAndItem() {
        BookingRequestDto getBooking = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        Assertions.assertEquals(getBooking.getId(), booking.getId());
    }

    @Test
    void mapperToBookingDtoWithBookerAndItemNull() {
        BookingRequestDto getBooking = BookingMapper.toBookingDtoWithBookerAndItem(null);
        Assertions.assertNull(getBooking);
    }



    @Test
    void mapperToBookingForItemDto() {
        BookingForItemDto getBooking = BookingMapper.toBookingForItemDto(booking);
        Assertions.assertEquals(getBooking.getId(), booking.getId());
    }

    @Test
    void mapperToBookingForItemDtoNull() {
        BookingForItemDto getBooking = BookingMapper.toBookingForItemDto(null);
        Assertions.assertNull(getBooking);
    }


}
