package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import app.config.DatabaseProperty;

@SpringBootApplication
public class RentCarApp {

	public static void main(String[] args) {
		
		SpringApplication.run(RentCarApp.class, args);

	}
	
}
