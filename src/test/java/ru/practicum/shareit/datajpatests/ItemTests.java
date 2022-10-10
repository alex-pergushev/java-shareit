package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ItemTests {

    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;
    private static Item item;
    private static User user;

    @BeforeEach
    public void setUp() {
        item = Item.builder()
                .name("дрель")
                .description("С ударным механизмом")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();

        user = User.builder()
                .name("Alex")
                .email("alex@pergushev.ru")
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveItem() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getName(), "дрель");
    }

    @Test
    void search() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Page<Item> items = repository.search("дре", Pageable.unpaged());
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.getSize(), 1);
    }

    @Test
    void findAllByOwnerOrderById() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        List<Item> items = repository.findAllByOwnerOrderById(user.getId());
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.size(), 1);
        Assertions.assertEquals(items.get(0).getName(), "дрель");
    }

    @Test
    void findIdByOwner() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Page<Long> ids = repository.findIdByOwner(user.getId(), Pageable.unpaged());
        Assertions.assertNotNull(ids);
        Assertions.assertEquals(ids.getSize(), 1);
    }

    @Test
    void findByRequestId() {
        userRepository.save(user);
        item.setOwner(user.getId());
        item.setRequest(8);
        repository.save(item);
        List<Item> items = repository.findByRequestId(8);
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.size(), 1);
    }

    @Test
    void findById() {
        userRepository.save(user);
        item.setOwner(user.getId());
        item = repository.save(item);
        Optional<Item> getItem = repository.findById(item.getId());
        Assertions.assertNotNull(getItem);
        Assertions.assertEquals(getItem.get().getDescription(), item.getDescription());
    }
}
