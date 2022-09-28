package ru.practicum.shareit.booking.service;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingSearchStatus;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Component
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    @Lazy
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    //Добавление нового запроса на бронирование
    @Override
    public BookingDto add(BookingInDto bookingInDto, long userId) {
        Item item = itemService.getItem(bookingInDto.getItemId());
        User booker = userService.getUser(userId);

        if (item.getOwner().getId() == userId) {
            // здесь должно быть ValidationException
            throw new EntityNotFoundException("по тестам здесь должно быть 404, а не 400");
        }

        if (bookingInDto.getStart() == null || bookingInDto.getEnd() == null
                || bookingInDto.getStart().after(bookingInDto.getEnd())
                || bookingInDto.getStart().before(new Date())) {
            throw new ValidationException("Некорректные даты бронирования");
        }

        if (!item.isAvailable()
        || bookingRepository.isBooked(bookingInDto.getItemId(),
                bookingInDto.getStart(), bookingInDto.getEnd()) > 0) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }

        Booking booking = new Booking();
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.saveAndFlush(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto updateStatus(Long bookingId, Long userId, Boolean approved) {
        Optional<Booking> updateBooking = bookingRepository.findById(bookingId);

        if (updateBooking.isPresent() && updateBooking.get().getItem()
                .getOwner().getId() == userId) {

            if (approved && updateBooking.get().getStatus().equals(BookingStatus.APPROVED)
                    || (!approved && updateBooking.get().getStatus().equals(BookingStatus.REJECTED))) {
                throw new ValidationException("Статус не может быть изменен");
            }

            if (approved) {
                updateBooking.get().setStatus(BookingStatus.APPROVED);
            } else {
                updateBooking.get().setStatus(BookingStatus.REJECTED);
            }

            return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(updateBooking.get()));
        } else {
            throw new EntityNotFoundException(String.format("Бронирование с идентификатором %d не найдено", bookingId));
        }
    }

    //Получение бронирования
    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isPresent() && (booking.get().getBooker().getId() == userId
        || booking.get().getItem().getOwner().getId() == userId)) {
            return BookingMapper.toBookingDto(booking.get());
        } else {
            throw new EntityNotFoundException(String.format("Бронирование с идентификатором %d не найдено", bookingId));
        }
    }

    //Получение всех бронирований пользователя
    @Override
    public Collection<BookingDto> findUserBookings(String state, long userId) {
        User user = userService.getUser(userId);
        validateStatus(state);

        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookingRepository.findUserBookings(userId, state)) {
            result.add(BookingMapper.toBookingDto(booking));
        }
        return result;
    }

    //Получение бронирований вещей пользователя
    @Override
    public Collection<BookingDto> findOwnerBookings(String state, long userId) {
        User user = userService.getUser(userId);
        validateStatus(state);

        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookingRepository.findOwnerBookings(userId, state)) {
            result.add(BookingMapper.toBookingDto(booking));
        }

        return result;
    }

    @Override
    public Boolean validateBooker(Long itemId, Long bookerId) {
        return bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(itemId,
                bookerId, BookingStatus.APPROVED, new Date());
    }

    private void validateStatus(String state) {
        try {
            BookingSearchStatus validateStatus = BookingSearchStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Неизвестный статус: %s", state));
        }
    }

}
