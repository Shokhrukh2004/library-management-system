package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.example.repository.BookRepository;
import org.example.repository.json.JsonBookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${app.books.file}")
    private String filePath;


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public BookRepository bookRepository() {
        return new JsonBookRepository(objectMapper(), filePath);
    }

}
