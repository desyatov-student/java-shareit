package ru.practicum.shareit.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateMapper {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    public String toString(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZONE);

        return date != null ? formatter.format(date) : null;
    }

    public Instant toInstant(String stringDate) {
        if (stringDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .withZone(ZoneId.systemDefault());
        ZonedDateTime localDateTime = LocalDateTime.parse(stringDate, formatter).atZone(ZONE);
        return localDateTime.toInstant();
    }

    public Instant toInstant(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.atZone(ZONE).toInstant();
    }

    public static Instant now() {
        return LocalDateTime.now().atZone(ZONE).toInstant();
    }
}