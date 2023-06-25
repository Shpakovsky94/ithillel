package org.example;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
