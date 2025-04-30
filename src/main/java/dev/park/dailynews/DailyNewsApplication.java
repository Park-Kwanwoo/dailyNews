package dev.park.dailynews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DailyNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyNewsApplication.class, args);
    }
}
