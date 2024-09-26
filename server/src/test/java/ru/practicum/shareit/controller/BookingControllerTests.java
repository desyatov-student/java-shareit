package ru.practicum.shareit.controller;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.helpers.TestData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        BookingDto booking = TestData.createBooking();
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

}