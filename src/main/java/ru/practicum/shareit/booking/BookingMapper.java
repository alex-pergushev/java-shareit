package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return BookingDto.builder()
                    .id((booking.getId()))
                    .booker(booking.getBooker().getId())
                    .itemId(booking.getItem().getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .status(booking.getStatus())
                    .build();
        } else {
            return null;
        }
    }

    public static BookingDtoWithBookerAndItem toBookingDtoWithBookerAndItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoWithBookerAndItem.builder()
                .id((booking.getId()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .itemDto(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingForItemDto(booking.getId(), booking.getBooker().getId());
    }
}
