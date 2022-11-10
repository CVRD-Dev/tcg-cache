package com.cvrd.tcgCache.TCGUI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.cvrd.tcgCache"})
public class TcguiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcguiApplication.class, args);
	}

}
