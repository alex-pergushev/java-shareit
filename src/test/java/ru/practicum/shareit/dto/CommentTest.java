package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;
    private static CommentDto commentDto = new CommentDto("комментарий");

    @Test
    void test() throws IOException {
        var result = jacksonTester.write(commentDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
    }
}
