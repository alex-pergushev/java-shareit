package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long id); //все бронирования пользователя

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end); //все завершенные бронирования

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Long id, BookingStatus status); //все бронирования в статусе ожидания подтверждения


    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = :id order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItemIdInOrderByStartDescCustom(Long id);

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = :id and end_date < :end order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItemIdInAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = :id and start_date > :start order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItemIdInAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = :id and start_date < :start and end_date > :end order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    @Query(value = "select * from bookings b join items i on b.item_id=i.id  " +
            "where owner_id = :id and status like :status order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItemIdInAndStatusOrderByStartDesc(Long id, String status); //все бронирования в статусе ожидания подтверждения

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = :id and b.status = :status order by b.start asc")
    List<BookingForItemDto> findAllByItemIdAndStatusOrderByStartAsc(Long id, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime end);
}
