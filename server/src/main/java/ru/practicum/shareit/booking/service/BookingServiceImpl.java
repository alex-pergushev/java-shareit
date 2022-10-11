package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private static final String NOT_FOUND = "Бронирование не найдено: ";

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User user = getUser(userId);               //проверка на существование пользователя
        Item item = getItem(bookingDto.getItemId()); //проверка на существование вещи
        validateItem(userId, bookingDto, item);

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    private void validateItem(Long userId, BookingDto bookingDto, Item item) {
        if (!item.isAvailable()) {
            log.debug("Вещь не доступна для бронирования.");
            throw new ValidationException("Вещь не доступна для бронирования.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.debug("Время окончания бронирования должно быть позже времени начала бронирования.");
            throw new ValidationException(
                    "Время окончания бронирования должно быть позже времени начала бронирования.");
        }
        if (userId == item.getOwner()) {
            log.debug("Владелец с идентификатором: {} не может забронировать свою вещь.", userId);
            throw new ObjectNotFoundException(String.format(
                    "Владелец с идентификатором: %d не может забронировать свою вещь.", userId));
        }
    }

    @Override
    public BookingRequestDto update(Long owner, boolean approved, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        validateOwnerBooking(owner, booking, item);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        BookingRequestDto bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    private void validateOwnerBooking(Long owner, Booking booking, Item item) {
        if (item.getOwner() != owner) {
            log.debug("Редактирование доступно только для владельца: {}", owner);
            throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.debug("Бронирование уже подтверждено.");
            throw new ValidationException("Бронирование уже подтверждено.");
        }
    }

    @Override
    public BookingRequestDto findById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (booker.getId() != userId && item.getOwner() != userId) {
            log.debug("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
            throw new ObjectNotFoundException("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
        }
        BookingRequestDto bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    @Override
    public List<BookingRequestDto> findAllById(long userId, BookingState state, int from, int size) {
        checkingPageParameters(from, size);
        User user = getUser(userId);               //проверка на существование пользователя
        Page<Booking> bookings = Page.empty();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                        userId, now, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
        }
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithBookerAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingRequestDto> findAllByOwner(long userId, BookingState state, int from, int size) {
        checkingPageParameters(from, size);
        User user = getUser(userId);
        Page<Booking> bookings = Page.empty();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdIn(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemIdInAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemIdInAndStartAfter(userId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfter(
                        userId, now, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemIdInAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemIdInAndStatus(userId, BookingStatus.REJECTED, pageable);
        }
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithBookerAndItem)
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return UserMapper.toUser(userService.findById(id));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }

    private void checkingPageParameters(int from, int size) {
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
