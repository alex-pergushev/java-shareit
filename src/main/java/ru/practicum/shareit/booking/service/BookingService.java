package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

/**
 * Интерфейс для работы с бронированиями
 */

public interface BookingService {

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingRequestDto update(Long owner, boolean approved, Long bookingId);

    BookingRequestDto findById(Long userId, Long bookingId);

    List<BookingRequestDto> findAllById(long userId, BookingState state, int from, int size);

    List<BookingRequestDto> findAllByOwner(long userId, BookingState state, int from, int size);
}
