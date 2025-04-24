package com.ddorokhov.origin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OriginApplication {

	public static void main(String[] args) {
		SpringApplication.run(OriginApplication.class, args);
	}

}
