package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingTests {

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;
    private static final LocalDateTime now = LocalDateTime.now();
    private static final LocalDateTime later = LocalDateTime.now().plusHours(2);

    private static BookingDto dto = BookingDto.builder()
            .id(1)
            .start(now)
            .end(later)
            .itemId(7)
            .booker(9)
            .status(BookingStatus.APPROVED)
            .build();

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo((int) dto.getItemId());
        assertThat(result).extractingJsonPathNumberValue("$.booker")
                .isEqualTo((int) dto.getBooker());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.APPROVED.toString());
    }
}
