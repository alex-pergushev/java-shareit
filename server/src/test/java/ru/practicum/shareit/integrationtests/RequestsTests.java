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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RequestsTests {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private static ItemDto itemDto;
    private static UserDto userDto;
    private static ItemRequestDto itemRequestDto;
    private static final LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
        itemDto = ItemDto.builder()
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
        itemRequestDto = ItemRequestDto.builder()
                .description("Описание")
                .requestor(1)
                .created(created)
                .build();
    }

    @AfterEach
    @Sql({"/test/resources/clean.sql"})
    void clean() {
    }

    @Test
    void save() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(getUser.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemRequestDto.setRequestor(getUser.getId());
        itemRequestService.create(getUser.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r", ItemRequest.class);
        List<ItemRequest> requests = query.getResultList();
        assertThat(requests.get(0).getId(), notNullValue());
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requests.get(0).getRequestor(), equalTo(itemRequestDto.getRequestor()));
        em.clear();
    }

    @Test
    void getRequests() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(getUser.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemRequestDto.setRequestor(getUser.getId());
        itemRequestService.create(getUser.getId(), itemRequestDto);
        List<ItemRequestDto> requests = itemRequestService.getRequests(getUser.getId());
        assertThat(requests.get(0).getId(), notNullValue());
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requests.get(0).getRequestor(), equalTo(itemRequestDto.getRequestor()));
        em.clear();
    }

    @Test
    void getAll() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(getUser.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemRequestDto.setRequestor(getUser.getId());
        itemRequestService.create(getUser.getId(), itemRequestDto);
        List<ItemRequestDto> requests = itemRequestService.getAll(getUser.getId(), 1, 10);
        assertThat(requests.size(), equalTo(0));
        em.clear();
    }

    @Test
    void getById() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(getUser.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemRequestDto.setRequestor(getUser.getId());
        ItemRequestDto getRequest = itemRequestService.create(getUser.getId(), itemRequestDto);
        ItemRequestDto request = itemRequestService.getById(getUser.getId(), getRequest.getId());
        assertThat(request.getDescription(), equalTo(itemRequestDto.getDescription()));
        em.clear();
    }

    @Test
    void addResponse() {
        UserDto getUser = userService.create(userDto);
        itemDto.setOwner(getUser.getId());
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);
        itemRequestDto.setRequestor(getUser.getId());
        ItemRequestDto getRequest = itemRequestService.create(getUser.getId(), itemRequestDto);
        ItemDto itemDto2 = ItemDto.builder()
                .id(itemDto.getId() + 1)
                .name("вещь по запросу")
                .description("Описание вещи по запросу")
                .owner(1)
                .available(true)
                .request(1)
                .build();
        itemService.create(getUser.getId(), itemDto2);
        ItemRequestDto request = itemRequestService.getById(getUser.getId(), getRequest.getId());
        assertThat(request.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(request.getItems().size(), equalTo(1));
        assertThat(request.getItems().get(0).getName(), equalTo(itemDto2.getName()));
        em.clear();
    }
}
