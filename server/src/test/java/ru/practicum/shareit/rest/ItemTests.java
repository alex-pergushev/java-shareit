package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemTests {

    @Mock
    private ItemService itemService;
    private final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private ItemController controller;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentDtoResponse commentDtoResponse;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("Дрель")
                .description("С ударным механизмом")
                .owner(1)
                .available(true)
                .request(0)
                .build();
        commentDto = new CommentDto("отзыв");
        commentDtoResponse = CommentDtoResponse.builder()
                .id(1)
                .text("Отзыв")
                .created(LocalDateTime.now())
                .author("Автор")
                .build();
    }

    @Test
    void saveNewItem() throws Exception {
        when(itemService.create(Mockito.anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(Mockito.anyLong(), Mockito.anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void findById() throws Exception {
        when(itemService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void findAllById() throws Exception {
        when(itemService.findAllById(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void search() throws Exception {
        when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "поиск"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), any(CommentDto.class)))
                .thenReturn(commentDtoResponse);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class));
    }
}
