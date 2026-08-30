package com.chronos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChronosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChronosApplication.class, args);
	}
}
