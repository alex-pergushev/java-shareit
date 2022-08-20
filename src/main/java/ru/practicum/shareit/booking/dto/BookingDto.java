package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * представление для бронирования вещи
 */
@Data
@Builder
public class BookingDto {
    private Long id;        //уникальный идентификатор бронирования
    private Date start;     // дата и время начала бронирования
    private Date end;       // дата и время конца бронирования
    private Long item;      // вещь, которую пользователь бронирует
    private Long booker;    // пользователь, который осуществляет бронирование
    private String status;  // статус бронирования. Может принимать одно из следующих значений:
                            // WAITING, APPROVED, REJECTED, CANCELED
}
