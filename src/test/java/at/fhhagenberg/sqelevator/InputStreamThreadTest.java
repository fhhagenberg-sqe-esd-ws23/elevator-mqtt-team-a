package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;

import org.junit.jupiter.api.Test;

class InputStreamThreadTest {

	@Test
	void testExitOnRightInput() throws IOException, InterruptedException {
		PipedInputStream input = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		try (OutputStreamWriter writer = new OutputStreamWriter(out)) {
			
			InputStreamThread thread = new InputStreamThread(input, "stop");	
			thread.start();			
			writer.write("stop\n");
			writer.flush();
			
			assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
				thread.join();
		    });

			assertTrue(thread.isExitRequest());
		}
	}
	
	@Test
	void testDoNotExitOnWrongInput() throws IOException, InterruptedException {
		PipedInputStream input = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		try (OutputStreamWriter writer = new OutputStreamWriter(out)) {
			
			InputStreamThread thread = new InputStreamThread(input, "stop");	
			thread.start();			
			writer.write("exit\n");
			writer.flush();
			
			Thread.sleep(100);
			assertTrue(thread.isAlive());
			assertFalse(thread.isExitRequest());

			// Stop thread
			writer.write("stop\n");
			writer.flush();

			assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
				thread.join();
		    });
		}
	}

}
