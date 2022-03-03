package com.example.imclient.clientcommand;

import java.util.Scanner;

public interface BaseCommand {

	/**
	 * execute command
	 * @param scanner
	 */
	void execute(Scanner scanner);


	/**
	 * identity key
	 * @return
	 */
	String getKey();


	/**
	 * obtain tip of command
	 * @return
	 */
	String getTip();
}
