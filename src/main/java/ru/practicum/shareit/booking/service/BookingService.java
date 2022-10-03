package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;

import java.util.List;

/**
 * Интерфейс для работы с бронированиями
 */

public interface BookingService {

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDtoWithBookerAndItem update(Long owner, boolean approved, Long bookingId);

    BookingDtoWithBookerAndItem findById(Long userId, Long bookingId);

    List<BookingDtoWithBookerAndItem> findAllById(long userId, BookingState state);

    List<BookingDtoWithBookerAndItem> findAllByOwner(long userId, BookingState state);
}
