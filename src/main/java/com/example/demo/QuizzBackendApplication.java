package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizzBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizzBackendApplication.class, args);
		System.out.println("QuiZy Backend is Running on port 8080...");
	}

}
