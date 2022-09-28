package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Boolean existsByItem_IdAndBooker_IdAndStatusAndEndBefore(Long itemId, Long bookerId,
                                                           BookingStatus status, Date end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    @Query(value = " select * from bookings b " +
            " where b.booker_id = :bookerId " +
            " and (:status = 'ALL' or " +
                 " :status = b.status or" +
                 " :status = case " +
                         "when start_date > current_date then 'FUTURE' " +
                         "when end_date < current_date then 'PAST' " +
                         "when current_date between start_date and end_date then 'CURRENT' " +
                        "end) " +
            " order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findUserBookings(Long bookerId, String status);

    @Query(value = " select * from bookings b, items i " +
            " where b.item_id = i.id " +
            " and i.owner_id = :ownerId " +
            " and (:status = 'ALL' or " +
                  ":status = b.status or " +
                  ":status = case " +
                          "when start_date > current_date then 'FUTURE' " +
                          "when end_date < current_date then 'PAST' " +
                          "when current_date between start_date and end_date then 'CURRENT' " +
                        "end) " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findOwnerBookings(Long ownerId, String status);

    @Query(value = " select count(*) from bookings " +
            "where item_id = :itemId " +
            "and (start_date between :startDate and :endDate or end_date between :startDate and :endDate) " +
            "and status = 'APPROVED'",
            nativeQuery = true)
    Long isBooked(Long itemId, Date startDate, Date endDate);
}
