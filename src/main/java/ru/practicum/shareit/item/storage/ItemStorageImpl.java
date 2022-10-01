package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStorageImpl implements ItemStorageCustom {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemStorage itemStorage;

    public ItemStorageImpl(@Lazy BookingRepository bookingRepository, @Lazy CommentRepository commentRepository,
                           @Lazy ItemStorage itemStorage) {
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto getByIdForResponse(long userId, long id) {
        ItemDto itemDto = ItemMapper.toItemDto(itemStorage.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Не найден предмет " + id)));
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        if (comments.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream()
                                        .map(CommentMapper::toCommentDtoResponse)
                                        .collect(Collectors.toList()));
        }
        addBookings(userId, itemDto);
        return itemDto;
    }

    private ItemDto addBookings(long userId, ItemDto itemDto) {
        if (userId == itemDto.getOwner()) {
            List<BookingForItemDto> bookings = getBooking(itemDto.getId());
            if (bookings.size() > 1) {
                itemDto.setLastBooking(bookings.get(0));
            }
            if (bookings.size() >= 2) {
                itemDto.setNextBooking(bookings.get(1));
            }
        }
        return itemDto;
    }

    private List<BookingForItemDto> getBooking(long itemId) {
        return bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);
    }
}
