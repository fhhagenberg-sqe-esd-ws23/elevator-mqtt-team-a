package at.fhhagenberg.sqelevator;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Class which starts a new thread and waits for a specific input line on the given input stream.
 * If the right input is detected the exit request flag is set and the thread runs out.
 */
public class ExitCommandThread extends Thread {	
	private final InputStream input;
	private final String exitLine;
	private volatile boolean exitRequest = false;

	/**
	 * Create and start a new ExitCommandThread.
	 * @param input the input stream to read from
	 * @param exitLine the input to detect on the input stream
	 */
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

	/**
	 * Thread run method. Do not call!
	 */
	@Override
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

	/**
	 * Provides the status of the exit request flag.
	 * @return whether the exit command was detected (true) or not (false).
	 */
	public boolean isExitRequest() {
		return exitRequest;
	}
}
