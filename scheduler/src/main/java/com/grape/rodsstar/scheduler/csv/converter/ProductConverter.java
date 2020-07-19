package com.grape.rodsstar.scheduler.csv.converter;

import com.grape.rodsstar.scheduler.enums.ProductType;
import com.opencsv.bean.AbstractBeanField;

public class ProductConverter  extends AbstractBeanField<ProductType, Integer> {

    @Override
    protected ProductType convert(String value) {
        return ProductType.valueOf(value.trim().toUpperCase());
    }
}
