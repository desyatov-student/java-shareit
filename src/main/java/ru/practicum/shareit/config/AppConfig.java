package ru.practicum.shareit.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import ru.practicum.shareit.utils.IdentifierGenerator;

@Configuration
@PropertySource("classpath:/application.properties")
public class AppConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentifierGenerator identifierGenerator() {
        return new IdentifierGenerator();
    }
}