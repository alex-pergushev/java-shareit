package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemStorage itemStorage;
    private final UserService userService;
    private static final String NOT_FOUND = "Бронирование не найдено: ";

    public BookingServiceImpl(BookingRepository bookingRepository, ItemStorage itemStorage, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User user = getUser(userId);               //проверка на существование пользователя
        Item item = getItem(bookingDto.getItemId()); //проверка на существование вещи
        if (!item.isAvailable()) {
            log.debug("Вещь не доступна для бронирования.");
            throw new ValidationException("Вещь не доступна для бронирования.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.debug("Время окончания бронирования должно быть позже времени начала бронирования.");
            throw new ValidationException("Время окончания бронирования должно быть позже времени начала бронирования.");
        }
        if (userId == item.getOwner()) {
            log.debug("Владедец не может забронировать свою вещь.");
            throw new ObjectNotFoundException("Владедец не может забронировать свою вещь.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithBookerAndItem update(Long owner, boolean approved, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (item.getOwner() != owner) {
            log.debug("Редактирование доступно только для владельца: {}", owner);
            throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.debug("Бронирование уже подтверждено.");
            throw new ValidationException("Бронирование уже подтверждено.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        BookingDtoWithBookerAndItem bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    @Override
    public BookingDtoWithBookerAndItem findById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (booker.getId() != userId && item.getOwner() != userId) {
            log.debug("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
            throw new ObjectNotFoundException("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
        }
        BookingDtoWithBookerAndItem bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    @Override
    public List<BookingDtoWithBookerAndItem> findAllById(long userId, BookingState state) {
        User user = getUser(userId);               //проверка на существование пользователя
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithBookerAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoWithBookerAndItem> findAllByOwner(long userId, BookingState state) {
        User user = getUser(userId);               //проверка на существование пользователя
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_IdInOrderByStartDescCustom(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_IdInAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_IdInAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_IdInAndStatusOrderByStartDesc(userId, BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_IdInAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED.toString());
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
        return itemStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }
}
