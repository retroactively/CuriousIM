package com.example.imclient.clientcommand;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Data
@Slf4j
@Service
public class ConsoleChatCommand implements BaseCommand {

	public static final String KEY = "3";

	private String toId;

	private String message;

	@Override
	public void execute(Scanner scanner) {
		System.out.println("please input the message (id:message): ");
		String[] info = null;
		while (true) {
			String input = scanner.next();
			info = input.trim().split(":");

			if (info.length != 2) {
				System.out.println("please input the message (id:message):");
			} else {
				break;
			}
		}

		toId = info[0];
		message = info[1];
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getTip() {
		return "chat";
	}
}
