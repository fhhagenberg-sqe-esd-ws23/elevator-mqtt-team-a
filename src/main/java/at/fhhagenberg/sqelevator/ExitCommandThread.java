package at.fhhagenberg.sqelevator;

import java.io.InputStream;
import java.util.Scanner;

public class ExitCommandThread extends Thread {	
	private final InputStream input;
	private final String exitLine;
	private volatile boolean exitRequest = false;
	
	public ExitCommandThread(InputStream input, String exitLine) {
		if(input == null) {
			throw new IllegalArgumentException("Input stream must not be null!");
		}
		
		if(exitLine == null) {
			throw new IllegalArgumentException("ExitLine must not be null!");
		}
		
		if(exitLine.isBlank()) {
			throw new IllegalArgumentException("ExitLine must not be blank!");
		}
		
		this.input = input;
		this.exitLine = exitLine;
	}
	
	public void run() {
		try (Scanner in = new Scanner(input)) {			
			while(true) {
				if(in.hasNextLine()) {
					String line = in.nextLine();
					
					if(exitLine.equals(line)) {
						exitRequest = true;
						return;
					}
				}
			}
		}
    }

	public boolean isExitRequest() {
		return exitRequest;
	}
}
