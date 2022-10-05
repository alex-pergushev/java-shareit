package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%')))" +
            " and i.isAvailable=TRUE")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByOwnerOrderById(Long userId);

    @Query("select distinct i.id from Item i left join Booking b on i.id = b.item.id where i.owner = ?1 order by i.id")
    Page<Long> findIdByOwner(long id, Pageable pageable);

    @Query("select i " +
            "from Item i where i.request = :id")
    List<Item> findByRequestId(long id);
}
