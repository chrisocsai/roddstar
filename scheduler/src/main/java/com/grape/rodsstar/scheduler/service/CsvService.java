package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.SchedulerException;
import com.grape.rodsstar.scheduler.csv.MappingStrategy;
import com.grape.rodsstar.scheduler.csv.OrderInput;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

@Service
public class CsvService {

    public List<OrderInput> parseOrders(InputStream inputStream) {
        HeaderColumnNameMappingStrategy<OrderInput> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
        mappingStrategy.setType(OrderInput.class);
        try(Reader reader = new InputStreamReader(inputStream)) {
            CsvToBean<OrderInput> cb = new CsvToBeanBuilder<OrderInput>(reader)
                    .withType(OrderInput.class)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            return cb.parse();
        } catch (IOException e) {
            throw new SchedulerException(e);
        }
    }

    public <T> String createOutput(List<T> items, Class<? extends T> clazz){
        MappingStrategy<T> mappingStrategy = new MappingStrategy<>();
        mappingStrategy.setType(clazz);
        StringWriter writer = new StringWriter();
        StatefulBeanToCsv<T> sbc = new StatefulBeanToCsvBuilder<T>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(mappingStrategy)
                .build();
        try {
            sbc.write(items);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new SchedulerException(e);
        }
        return writer.toString();
    }
}
