package ru.avkurbatov_home.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateTypeConverter {
    private DateTypeConverter() {}

    public static LocalDate toLocalDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(java.sql.Timestamp timestamp){
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

}
