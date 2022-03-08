package com.example.imclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ImClientApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ImClientApplication.class, args);
		CommandController controller = context.getBean(CommandController.class);
		controller.initCommandMap();

		try {
			controller.startCommandThread();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
