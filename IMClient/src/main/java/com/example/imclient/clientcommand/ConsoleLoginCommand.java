package com.example.imclient.clientcommand;

import java.util.Scanner;

public class ConsoleLoginCommand implements BaseCommand{

	private static final String KEY = "2";

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
