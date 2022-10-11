package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

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
public class BookingTests {

    @Mock
    private BookingService bookingService;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    @InjectMocks
    private BookingController controller;
    private MockMvc mvc;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(3);
    private static final String startStr = "2022-11-04T12:00:00";
    private static final String endStr = "2022-11-04T18:00:00";

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(1)
                .booker(2)
                .status(BookingStatus.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemDto(new ItemDto())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void saveNewBooking() throws Exception {
        when(bookingService.create(Mockito.anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content("{\"itemId\":2,\"start\":\"" + startStr + "\",\"" + endStr + "\":\"{{end}}\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.update(Mockito.anyLong(), any(boolean.class), Mockito.anyLong()))
                .thenReturn(bookingRequestDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void findById() throws Exception {
        when(bookingService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingRequestDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingRequestDto.getId()), Long.class))
                .andDo(print());
    }

    @Test
    void findAllById() throws Exception {
        when(bookingService.findAllById(
                Mockito.anyLong(), any(BookingState.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingRequestDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingRequestDto.getId()), Long.class))
                .andDo(print());
    }

    @Test
    void findAllByOwner() throws Exception {
        when(bookingService.findAllByOwner(
                Mockito.anyLong(), any(BookingState.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingRequestDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingRequestDto.getId()), Long.class))
                .andDo(print());
    }
}
