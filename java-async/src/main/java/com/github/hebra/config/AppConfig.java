package com.github.hebra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@Import({
        DataJdbcConfiguration.class,
        SpringDataJdbcConfiguration.class
})
@EnableJdbcRepositories("com.github.hebra.repository")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public Scheduler reactiveScheduler() {
        return Schedulers.boundedElastic();
    }

}
