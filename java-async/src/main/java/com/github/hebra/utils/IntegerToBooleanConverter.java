package com.github.hebra.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

    // Converting 0/1 to boolean
    @Override
    public Boolean convert(Integer source) {
        return BooleanUtils.toBooleanObject(source);
    }
}

