package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long id); //все бронирования пользователя

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end); //все завершенные бронирования

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(
            Long id, BookingStatus status); //все бронирования в статусе ожидания подтверждения


    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = ?1 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_IdInOrderByStartDescCustom(Long id);

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = ?1 and end_date < ?2 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_IdInAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = ?1 and start_date > ?2 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_IdInAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования

    @Query(value = "select * from bookings b join items i on b.item_id=i.id " +
            "where owner_id = ?1 and start_date < ?2 and end_date > ?3 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_IdInAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    @Query(value = "select * from bookings b join items i on b.item_id=i.id  " +
            "where owner_id = ?1 and status like ?2 order by start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_IdInAndStatusOrderByStartDesc(Long id, String status); //все бронирования в статусе ожидания подтверждения

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = ?1 and b.status = ?2 order by b.start asc")
    List<BookingForItemDto> findAllByItem_IdAndStatusOrderByStartAsc(Long id, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime end);
}
