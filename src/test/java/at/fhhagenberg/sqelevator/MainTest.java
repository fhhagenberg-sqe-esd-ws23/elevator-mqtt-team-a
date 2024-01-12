package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import at.fhhagenberg.sqelevator.mqttadapter.Main;

class MainTest {
	
	@Test
	void testStartWithElevatorSimulatorNullArgs() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(null, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}
	
	@Test
	void testStartWithElevatorSimulatorEmptyArgs() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[0];
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}

	@Test
	void testStartWithPlcMock() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[1];
		args[0] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using RMI API Mock."));
	}
	
	@Test
	void testStartWithPlcMockWrongArgumentNumber() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[2];
		args[1] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}
	
	@Test
	void testStartWithPlcMockWrongArgumentContent() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[2];
		args[0] = "rmimok";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}

}
