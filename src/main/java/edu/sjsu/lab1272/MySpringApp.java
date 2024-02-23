package edu.sjsu.lab1272;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/*
    Structuring of Spring application, application runner, and command set was
    inspired from the usage recommendation given by picocli for springboot
    https://github.com/remkop/picocli/blob/main/picocli-spring-boot-starter/README.md
 */
@SpringBootApplication
public class MySpringApp {
    static Logger logger = LoggerFactory.getLogger(MySpringApp.class);
    @Bean
    CanvasClient someService() {
        return new CanvasClient();
    }

    public static void main(String[] args) {
        logger.debug("Starting main server");
        SpringApplication.run(MySpringApp.class, args);
    }
}
