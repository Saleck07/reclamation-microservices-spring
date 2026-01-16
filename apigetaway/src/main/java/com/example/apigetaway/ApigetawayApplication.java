package com.example.apigetaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApigetawayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApigetawayApplication.class, args);
	}

}
