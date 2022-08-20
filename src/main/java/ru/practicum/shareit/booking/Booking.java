package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * бронирование вещи
 */
@Data
@Builder
public class Booking {
    private long id;                //Уникальный идентификатор бронирования
    private Date start;             // Дата и время начала бронирования
    private Date end;               // Дата и время конца бронирования
    private Item item;              // Вещь, которую пользователь бронирует
    private User booker;            // Пользователь, который осуществляет бронирование
    private BookingStatus status;   // Статус бронирования. Может принимать одно из следующих значений:
                                    // WAITING, APPROVED, REJECTED, CANCELED
}
