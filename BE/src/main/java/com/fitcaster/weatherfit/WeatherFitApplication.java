// WeatherFitApplication

package com.fitcaster.weatherfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WeatherFitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherFitApplication.class, args);
	}

}
