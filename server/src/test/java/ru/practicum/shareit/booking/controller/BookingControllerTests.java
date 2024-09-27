package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.helpers.TestData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.WebConstant.HEADER_X_SHARER_USER_ID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getById_returnResponse_Success() throws Exception {
        // Given
        long bookingId = 1L;
        long userId = 2L;
        BookingDto booking = TestData.createBookingDto();
        when(service.getById(bookingId, userId)).thenReturn(booking);

        // When
        MvcResult result = mvc.perform(get("/bookings/" + bookingId)
                .header(HEADER_X_SHARER_USER_ID, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        BookingDto actualBooking = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getById(bookingId, userId);
        Mockito.verifyNoMoreInteractions(service);
        assertThat(actualBooking).isEqualTo(booking);
    }

    @Test
    void getBookings_returnBookingsByBooker_Success() throws Exception {
        // Given
        long userId = 2L;
        List<BookingDto> bookings = TestData.createBookings();
        when(service.getBookingsByBooker(userId, BookingState.ALL)).thenReturn(bookings);

        // When
        MvcResult result = mvc.perform(get("/bookings?state=ALL")
                .header(HEADER_X_SHARER_USER_ID, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<BookingDto> actualBookings = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getBookingsByBooker(userId, BookingState.ALL);
        Mockito.verifyNoMoreInteractions(service);
        assertThat(actualBookings).isEqualTo(bookings);
    }

    @Test
    void getBookings_returnBookingsByOwner_Success() throws Exception {
        // Given
        long userId = 2L;
        List<BookingDto> bookings = TestData.createBookings();
        when(service.getBookingsByOwner(userId, BookingState.ALL)).thenReturn(bookings);

        // When
        MvcResult result = mvc.perform(get("/bookings/owner?state=ALL")
                .header(HEADER_X_SHARER_USER_ID, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<BookingDto> actualBookings = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).getBookingsByOwner(userId, BookingState.ALL);
        Mockito.verifyNoMoreInteractions(service);
        assertThat(actualBookings).isEqualTo(bookings);
    }

    @Test
    void create_returnResponse_RequestIsValid() throws Exception {
        // Given
        long userId = 2L;
        NewBookingRequest request = new NewBookingRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5)
        );
        BookingDto booking = TestData.createBookingDto();
        when(service.create(userId, request)).thenReturn(booking);

        // When
        MvcResult result = mvc.perform(
                post("/bookings")
                        .header(HEADER_X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        BookingDto actualBooking = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).create(userId, request);
        Mockito.verifyNoMoreInteractions(service);
        assertThat(actualBooking).isEqualTo(booking);
    }

    @Test
    void create_throwException_RequestIsNotValid() throws Exception {
        // Given
        long userId = 2L;
        NewBookingRequest request = new NewBookingRequest(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5)
        );

        // When
        mvc.perform(
                post("/bookings")
                        .header(HEADER_X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.violations", containsInAnyOrder(
                Map.of("fieldName", "start","message", "must be a date in the present or in the future")
        )))
        .andReturn();

        // Then
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void approve_returnResponse_RequestIsValid() throws Exception {
        // Given
        long userId = 2L;
        long bookingId = 3L;
        NewBookingRequest request = new NewBookingRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5)
        );
        BookingDto booking = TestData.createBookingDto();
        when(service.approve(userId, bookingId, true)).thenReturn(booking);

        // When
        MvcResult result = mvc.perform(
                        patch("/bookings/" + bookingId + "?approved=true")
                                .header(HEADER_X_SHARER_USER_ID, userId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        BookingDto actualBooking = objectMapper.readValue(json, new TypeReference<>(){});

        // Then
        Mockito.verify(service).approve(userId, bookingId, true);
        Mockito.verifyNoMoreInteractions(service);
        assertThat(actualBooking).isEqualTo(booking);
    }
}