package com.example.fund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.domain")
@EnableJpaRepositories(basePackages = "com.example")
@EntityScan(basePackages = "com.example")
public class FundApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundApplication.class, args);
	}

}
