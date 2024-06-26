package com.plenigo.nasashell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NasaShellApplication {

	public static void main(String[] args) {
		SpringApplication.run(NasaShellApplication.class, args);
	}

}
