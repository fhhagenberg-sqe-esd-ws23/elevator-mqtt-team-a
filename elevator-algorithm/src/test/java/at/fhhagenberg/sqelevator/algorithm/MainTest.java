package at.fhhagenberg.sqelevator.algorithm;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;

import at.fhhagenberg.sqelevator.ElevatorProperties;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;


public class MainTest {	
	
    private void waitUntilAssertsTrue(BooleanSupplier func, int tryTimes, int waitBetweenTriesMs) throws InterruptedException {    	
    	boolean result = false;
		for(int i = 0; i < tryTimes; ++i) {
			if(func.getAsBoolean()) {
				result = true;
				break;
			}
			Thread.sleep(waitBetweenTriesMs);
		}						
		assertTrue(result);
    }
	
	@Test
	void testStartWithOutSendingMessages() throws IOException, InterruptedException {
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
		assertTrue(outputString.contains("Waiting for initial mqtt messages to arrive.\n"));
	}
	
	@Test
	void testStartWithSendingMessages() throws IOException, InterruptedException, ExecutionException {
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
		ElevatorsMqttClient mqtt = new ElevatorsMqttClient(props.getMqttAddress(), props.getMqttPort());
		
		
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
			

	    	mqtt.connect();
	    	
	    	mqtt.publishFloorHeight(10);
	    	
	    	mqtt.publishNumberOfElevators(3);
	    	mqtt.publishNumberOfElevators(2);
	    	
	    	
	    	waitUntilAssertsTrue(()->{
	    		String outputString = output.toString();
	    		return outputString.contains("got initial data...");
	    	},10,1000);
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
	}

}
