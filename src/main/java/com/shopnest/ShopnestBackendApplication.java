package com.shopnest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShopnestBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopnestBackendApplication.class, args);
	}

}
