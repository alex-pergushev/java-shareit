package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long>, ItemStorageCustom {
    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.isAvailable=TRUE")
    List<Item> search(String text);

    List<Item> findAllByOwnerOrderById(Long userId);

    @Query(value = "select distinct i.id from bookings b right join items i on i.id = b.item_id " +
            "where owner_id = ?1 order by i.id", nativeQuery = true)
    List<Long> findIdByOwner(long id);
}
