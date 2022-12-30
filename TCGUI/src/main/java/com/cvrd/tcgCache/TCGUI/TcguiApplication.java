package com.cvrd.tcgCache.TCGUI;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.annotation.WebServlet;

@Push
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.cvrd.tcgCache"})
public class TcguiApplication extends SpringBootServletInitializer  implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(TcguiApplication.class, args);
	}

}
