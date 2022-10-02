package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * для работы с бронированиями
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Добавление нового запроса на бронирование. Запрос может быть создан любым пользователем, а затем подтверждён
     * владельцем вещи.
     * Эндпоинт — POST /bookings.
     * После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     */

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
     * Затем статус бронирования становится либо APPROVED, либо REJECTED.
     * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать
     * значения true или false.
     */

    @PatchMapping("/{bookingId}")
    public BookingDtoWithBookerAndItem update(@RequestHeader("X-Sharer-User-Id") long owner,
                                          @RequestParam("approved") boolean approved,
                                          @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.update(owner, approved, bookingId);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус). Может быть выполнено либо автором бронирования,
     * либо владельцем вещи, к которой относится бронирование.
     * Эндпоинт — GET /bookings/{bookingId}.
     */
    @GetMapping("/{bookingId}")
    public BookingDtoWithBookerAndItem findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.findById(userId, bookingId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя.
     * Эндпоинт — GET /bookings?state={state}.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения:
     * CURRENT (англ. «текущие»),
     * PAST (англ. «завершённые»),
     * FUTURE (англ. «будущие»),
     * WAITING (англ. «ожидающие подтверждения»),
     * REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping
    public List<BookingDtoWithBookerAndItem> findAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         BookingState state) throws ObjectNotFoundException {
        return bookingService.findAllById(userId, state);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     * Эндпоинт — GET /bookings/owner?state={state}.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи.
     * Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping("/owner")
    public List<BookingDtoWithBookerAndItem> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(required = false, defaultValue = "ALL")
                                                            BookingState state) throws ObjectNotFoundException {
        return bookingService.findAllByOwner(userId, state);
    }
}
