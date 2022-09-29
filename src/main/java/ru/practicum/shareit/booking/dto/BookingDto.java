package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
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
    @Future
    private LocalDateTime start;//дата и время начала бронирования;
    @Future
    private LocalDateTime end;  //дата и время конца бронирования;
    private long itemId;      // вещь, которую пользователь бронирует
    private long booker;      // пользователь, который осуществляет бронирование
    private BookingStatus status;  // статус бронирования. Может принимать одно из следующих значений:
    // WAITING, APPROVED, REJECTED, CANCELED
}
