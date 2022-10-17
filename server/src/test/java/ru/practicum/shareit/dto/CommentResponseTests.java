package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDtoResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentResponseTests {

    @Autowired
    private JacksonTester<CommentDtoResponse> jacksonTester;
    private static final LocalDateTime now = LocalDateTime.now();

    private static CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
            .id(1)
            .text("Комментарий")
            .author("Автор")
            .created(now)
            .build();

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(commentDtoResponse);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDtoResponse.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDtoResponse.getAuthor());
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) commentDtoResponse.getId());
    }
}
