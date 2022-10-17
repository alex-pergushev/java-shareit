package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * представление для бронирования вещи
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;        //уникальный идентификатор бронирования
    private LocalDateTime start;//дата и время начала бронирования;
    private LocalDateTime end;  //дата и время конца бронирования;
    private long itemId;      // вещь, которую пользователь бронирует
    private long bookerId;
    private UserDto booker;      // пользователь, который осуществляет бронирование
    private ItemDto item;
    private BookingStatus status;  // статус бронирования. Может принимать одно из следующих значений:
    // WAITING, APPROVED, REJECTED, CANCELED
}
