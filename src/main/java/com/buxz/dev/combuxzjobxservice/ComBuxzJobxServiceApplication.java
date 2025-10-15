package com.buxz.dev.combuxzjobxservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class ComBuxzJobxServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComBuxzJobxServiceApplication.class, args);
	}

}
