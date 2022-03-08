package com.example.imclient.clientcommand;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;


@Data
@Slf4j
@Service
public class ClientMenuCommand implements BaseCommand{

	private static final String KEY = "1";

	private String commandsShow;

	private String commandInput;

	@Override
	public void execute(Scanner scanner) {
		System.err.println("Please input one command: ");
		System.err.println(commandsShow);
		commandInput = scanner.next();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getTip() {
		return "show all commands";
	}
}
