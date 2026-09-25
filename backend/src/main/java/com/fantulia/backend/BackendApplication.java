package com.fantulia.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication: shorthand for @Configuration + @EnableAutoConfiguration
// + @ComponentScan, i.e. "this is the app's entry point, auto-configure beans
// from the classpath, and scan this package and below for components."
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
