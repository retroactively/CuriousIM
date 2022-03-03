package com.example.imserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ImServerApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ImServerApplication.class, args);
		ChatServer server = context.getBean(ChatServer.class);
		server.run();
	}

}
