package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingForItemTests {

    @Autowired
    private JacksonTester<BookingForItemDto> jacksonTester;
    private BookingForItemDto dto = new BookingForItemDto(7L, 9L);

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo((int) dto.getBookerId());
    }
}
