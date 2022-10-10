package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
public class UserTests {

    private final EntityManager em;
    private final UserService userService;
    private static UserDto userDto;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
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
    void saveUser() {
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        UserDto getUser = userService.create(userDto);
        getUser.setEmail("peter2@ya.ru");
        userService.update(getUser.getId(), getUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", getUser.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(getUser.getName()));
        assertThat(user.getEmail(), equalTo(getUser.getEmail()));
    }

    @Test
    void findAll() {
        userService.create(userDto);
        userDto.setEmail("peter2@ya.ru");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(2));
    }

    @Test
    void findById() {
        UserDto getUser = userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", getUser.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(getUser.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}
