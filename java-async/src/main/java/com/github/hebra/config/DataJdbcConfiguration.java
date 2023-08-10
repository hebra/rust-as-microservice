package com.github.hebra.config;

import com.github.hebra.utils.IntegerToBooleanConverter;
import com.github.hebra.utils.StringToBooleanConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;

@Configuration
public class DataJdbcConfiguration extends AbstractJdbcConfiguration {

    @Override
    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new StringToBooleanConverter(),
                new IntegerToBooleanConverter()
        ));
    }
}
