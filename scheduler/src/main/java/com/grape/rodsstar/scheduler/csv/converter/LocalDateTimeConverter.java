package com.grape.rodsstar.scheduler.csv.converter;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

import static java.time.LocalDateTime.parse;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, Integer> {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("MM.dd. HH:mm")
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().get(ChronoField.YEAR))
            .toFormatter();

    @Override
    protected LocalDateTime convert(String s) {
        LocalDateTime result = parse(s.trim(), formatter);
        if(result.isBefore(LocalDateTime.now())){
            return result.plusYears(1);
        }
        return result;
    }

    @Override
    protected String convertToWrite(Object value) {
        if(value instanceof TemporalAccessor) {
            return formatter.format((TemporalAccessor)value);
        }
        return Objects.toString(value, "");
    }
}
