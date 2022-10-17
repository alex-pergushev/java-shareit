package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
public class UserTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    private static User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Alex");
        user.setEmail("alex@pergushev.ru");
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveUser() {
        userRepository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getName(), "Alex");
    }

    @Test
    void updateUser() {
        userRepository.save(user);
        user.setEmail("alexander@pergushev.ru");
        userRepository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getEmail(),"alexander@pergushev.ru");
    }
}
