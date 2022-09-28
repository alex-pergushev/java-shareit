package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.util.Collection;

/**
 * Интерфейс для работы с бронированиями
 */

public interface BookingService {

    BookingDto add(BookingInDto bookingInDto, long userId);
    BookingDto updateStatus(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);
    Collection<BookingDto> findUserBookings(String state, long userId);
    Collection<BookingDto> findOwnerBookings(String state, long userId);

    Boolean validateBooker(Long itemId, Long bookerId);
}
