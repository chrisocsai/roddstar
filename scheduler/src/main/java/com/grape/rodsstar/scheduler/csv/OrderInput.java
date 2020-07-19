package com.grape.rodsstar.scheduler.csv;

import com.grape.rodsstar.scheduler.csv.converter.IntegerConverter;
import com.grape.rodsstar.scheduler.csv.converter.LocalDateTimeConverter;
import com.grape.rodsstar.scheduler.csv.converter.ProductConverter;
import com.grape.rodsstar.scheduler.enums.ProductType;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class OrderInput {
    @CsvBindByName(column = "Azonosító")
    private String id;
    @CsvCustomBindByName(column = "Termék", converter = ProductConverter.class)
    private ProductType productType;
    @CsvCustomBindByName(column = "Darabszám", converter = IntegerConverter.class)
    private Integer quantity;
    @CsvCustomBindByName(column = "Határidő", converter = LocalDateTimeConverter.class)
    private LocalDateTime termDate;
    @CsvCustomBindByName(column = "Profit/db (Ft)", converter = IntegerConverter.class)
    private Integer profitPerUnit;
    @CsvCustomBindByName(column = "Késési büntetés/nap (össz) (Ft)", converter = IntegerConverter.class)
    private Integer penaltyPerDay;
}
