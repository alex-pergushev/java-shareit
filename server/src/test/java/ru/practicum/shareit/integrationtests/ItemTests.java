package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemTests {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private static ItemDto itemDto;
    private static UserDto userDto;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
        itemDto = ItemDto.builder()
                .id(1)
                .name("Дрель")
                .description("С ударным механизмом")
                .owner(1)
                .available(true)
                .request(0)
                .build();
        userDto = UserDto.builder()
                .name("Alex")
                .email("alex@pergushev.ru")
                .build();
    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }

    @Test
    void saveItem() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(userDto.getId());
        itemService.create(getUser.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItem() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(userDto.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemDto.setDescription("новое описание");
        itemService.update(getUser.getId(), getItem.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void findById() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(userDto.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", getItem.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void findAllById() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(userDto.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemDto.setId(getItem.getId() + 1);
        itemService.create(getUser.getId(), itemDto);
        List<ItemDto> items = itemService.findAllById(getUser.getId(), 1, 10);
        assertThat(items.size(), equalTo(2));
    }

    @Test
    void search() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(userDto.getId());
        itemService.create(getUser.getId(), itemDto);
        List<ItemDto> items = itemService.search("дре", 1, 10);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo(itemDto.getName()));
    }
}
