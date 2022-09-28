package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findItemsByOwnerIdOrderById(Long id);

    @Query("select it from Item it " +
            "where (upper(it.name) like upper(concat('%', :text, '%')) " +
            "or upper(it.description) like upper(concat('%', :text, '%'))) " +
            "and it.available = true")
    List<Item> search(String text);

    @Query(value = "select bl.id, bl.booker_id " +
            " from (select i.id," +
            " (select max(b.start_date) " +
            " from bookings b " +
            " where b.start_date < :nowDate " +
            " and i.id = b.item_id) last_booking_date from items i) ib " +
            " LEFT JOIN bookings bl ON bl.item_id = ib.id AND bl.start_date = ib.last_booking_date " +
            " where ib.id = :itemId ",
            nativeQuery = true)
    List<Object[]> findLastBooking(Long itemId, Date nowDate);

    @Query(value = "select bl.id, bl.booker_id " +
            " from (select i.id," +
            " (select min(b.start_date) " +
            " from bookings b " +
            " where b.start_date > :nowDate" +
            " and i.id = b.item_id) next_booking_date from items i) ib " +
            " LEFT JOIN bookings bl ON bl.item_id = ib.id AND bl.start_date = ib.next_booking_date " +
            " where ib.id = :itemId ",
            nativeQuery = true)
    List<Object[]> findNextBooking(Long itemId, Date nowDate);
}
