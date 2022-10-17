package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserTests {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    private static UserDto userDto = UserDto.builder()
            .id(1)
            .name("Alex")
            .email("alex@pergushev.ru")
            .build();

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(userDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) userDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}
