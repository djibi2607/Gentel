package com.abdoul.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HoletServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoletServiceApplication.class, args);
	}

}
