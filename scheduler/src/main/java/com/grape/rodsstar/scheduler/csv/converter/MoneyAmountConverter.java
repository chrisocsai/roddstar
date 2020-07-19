package com.grape.rodsstar.scheduler.csv.converter;

import com.opencsv.bean.AbstractBeanField;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class MoneyAmountConverter extends AbstractBeanField<Long, Integer> {

    @Override
    protected Long convert(String s) {
        return null;
    }

    @Override
    protected String convertToWrite(Object value) {
        if(value instanceof Number){
            Locale hun = new Locale("hu", "HU");
            NumberFormat hunFormat = NumberFormat.getCurrencyInstance(hun);
            return hunFormat.format(value);
        }
        return Objects.toString(value, "");
    }
}
