package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;

import org.junit.jupiter.api.Test;

class ExitCommandThreadTest {

	@Test
	void testExitOnRightInput() throws IOException, InterruptedException {
		PipedInputStream input = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		try (OutputStreamWriter writer = new OutputStreamWriter(out)) {
			
			ExitCommandThread thread = new ExitCommandThread(input, "stop");	
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
			
			ExitCommandThread thread = new ExitCommandThread(input, "stop");	
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
	
	@Test
	void testNullInput() throws IOException, InterruptedException {
		PipedInputStream input = null;

		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()-> new ExitCommandThread(input, "stop"));	
		assertEquals("Input stream must not be null!",ex.getMessage());
	}
	
	@Test
	void testNullExitline() throws IOException, InterruptedException {
		PipedInputStream input = new PipedInputStream();

		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()-> new ExitCommandThread(input, null));	
		assertEquals("ExitLine must not be null!",ex.getMessage());
	}
	
	@Test
	void testEmptyExitLine() throws IOException, InterruptedException {
		PipedInputStream input = new PipedInputStream();

		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()-> new ExitCommandThread(input, ""));	
		assertEquals("ExitLine must not be blank!",ex.getMessage());
	}

}
