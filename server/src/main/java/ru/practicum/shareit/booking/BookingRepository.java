package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerId(
            long id, Pageable pageable); //все бронирования пользователя

    Page<Booking> findAllByBookerIdAndEndBefore(
            Long id, LocalDateTime end, Pageable pageable); //все завершенные бронирования

    Page<Booking> findAllByBookerIdAndStartAfter(
            Long id, LocalDateTime start, Pageable pageable); //все будущие бронирования

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long id, LocalDateTime start, LocalDateTime end, Pageable pageable); //все текущие бронирования

    Page<Booking> findAllByBookerIdAndStatus(
            Long id, BookingStatus status, Pageable pageable); //все бронирования в статусе ожидания подтверждения


    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = :id order by b.start desc")
    Page<Booking> findAllByItemIdIn(Long id, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = :id " +
            "and b.end < :end order by b.start desc")
    Page<Booking> findAllByItemIdInAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = :id " +
            "and b.start > :start order by b.start desc")
    Page<Booking> findAllByItemIdInAndStartAfter(
            Long id, LocalDateTime start, Pageable pageable); //все будущие бронирования

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = :id " +
            "and b.start < :start and b.end > :end order by b.start desc")
    Page<Booking> findAllByItemIdInAndStartBeforeAndEndAfter(
            Long id, LocalDateTime start, LocalDateTime end, Pageable pageable); //все текущие бронирования

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = :id " +
            "and b.status = :status order by b.start desc")
    Page<Booking> findAllByItemIdInAndStatus(
            Long id, BookingStatus status, Pageable pageable); //все бронирования в статусе ожидания подтверждения

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = :id and b.status = :status order by b.start asc")
    List<BookingForItemDto> findAllByItemIdAndStatus(Long id, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(
            Long userId, Long itemId, LocalDateTime end);
}
