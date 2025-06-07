package com.cdac.dreamblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DreamblogApplication {

	public static void main(String[] args) {
		System.out.println("Starting.....");
		SpringApplication.run(DreamblogApplication.class, args);
	}

}
//entry point SpringApplication.run