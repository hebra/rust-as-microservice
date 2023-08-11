package com.github.hebra.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToBooleanConverter implements Converter<String, Boolean> {

    // Converting Y/N to boolean
    @Override
    public Boolean convert(String source) {
        return BooleanUtils.toBooleanObject(source);
    }
}
