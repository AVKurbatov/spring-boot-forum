package ru.avkurbatov_home.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public final class StringTypeConverter {
    private StringTypeConverter() {}

    public static Integer toInteger(Object value) {
        return toInteger(value.toString());
    }

    public static Integer toInteger(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            log.error("Value {} must be integer", value, e);
            return null;
        }
    }

    public static String fromInteger(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
