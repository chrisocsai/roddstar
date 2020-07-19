package com.grape.rodsstar.scheduler.csv.converter;

import com.opencsv.bean.AbstractBeanField;

public class IntegerConverter extends AbstractBeanField<Integer, Integer> {
    @Override
    protected Integer convert(String value) {
        return Integer.valueOf(value.replaceAll("[^\\d]", ""));
    }
}
