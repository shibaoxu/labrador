package com.labrador.turbineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableTurbine
public class TurbineserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineserviceApplication.class, args);
	}
}
