package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;

/**
 * бронирование вещи
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                //Уникальный идентификатор бронирования

    @Column(name = "start_date")
    private Date start;             // Дата и время начала бронирования

    @Column(name = "end_date")
    private Date end;               // Дата и время конца бронирования

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;              // Вещь, которую пользователь бронирует

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;            // Пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    private BookingStatus status;   // Статус бронирования. Может принимать одно из следующих значений:
                                    // WAITING, APPROVED, REJECTED, CANCELED
}
