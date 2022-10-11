package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemTests {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    private static ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("дрель")
            .description("С ударным механизмом")
            .owner(1)
            .available(true)
            .request(0)
            .build();

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(itemDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.owner");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.owner")
                .isEqualTo((int) itemDto.getId());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo((int) itemDto.getRequest());
    }
}
