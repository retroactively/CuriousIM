package com.example.imclient.clientcommand;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Slf4j
@Data
@Service
public class ConsoleLoginCommand implements BaseCommand{

	public static final String KEY = "2";

	private String userName;

	private String passWord;

	@Override
	public void execute(Scanner scanner) {
		System.out.println("please input user info(username:passsword): ");
		String[] info = null;
		while (true) {
			String input = scanner.next();
			info = input.trim().split(":");
			if (info.length != 2) {
				System.out.println("Please input info as the example format");
			} else {
				break;
			}
		}

		userName = info[0];
		passWord = info[1];
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getTip() {
		return "Login";
	}
}
