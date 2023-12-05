package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

class ElevatorsMqttAdapterTest {

	@Test
	void testNumberOfUpdaters() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		when(plc.getElevatorNum()).thenReturn(4);
		when(plc.getFloorNum()).thenReturn(5);
		Building building = new Building(plc);
		
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals(9, adapter.getUpdaters().length);
	}

	
	@Test
	void testUpdateTimerPeriodSetAndGet() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals(250, adapter.getUpdateTimerPeriodMs());
		
		adapter.setUpdateTimerPeriodMs(155);
		
		assertEquals(155, adapter.getUpdateTimerPeriodMs());
	}
	
	@Test
	void testUpdateTimerPeriodSetException() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals(250, adapter.getUpdateTimerPeriodMs());
		
		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()->adapter.setUpdateTimerPeriodMs(0));
		assertEquals("Update timer period must be greater than 0!", ex.getMessage());
	}
	
	@Test
	void testGetExitLine() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals("exit", adapter.getExitLine());
		
		adapter.setExitLine("hello");
		
		assertEquals("hello", adapter.getExitLine());
	}

	@Test
	void testSetExitLineNull() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()->adapter.setExitLine(null));
		assertEquals("ExitLine must not be null!", ex.getMessage());
	}

	@Test
	void testSetExitLineBlank() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		IllegalArgumentException ex =assertThrowsExactly( IllegalArgumentException.class, ()->adapter.setExitLine(""));
		assertEquals("ExitLine must not be blank!", ex.getMessage());
	}

	@Test
	void testExitOnRightInput() throws IOException, InterruptedException, ExecutionException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						adapter.run(input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			inWriter.write("exit\n");
			inWriter.flush();
			
			assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
				t1.join();
			});
		}
	}

	@Test
	void testUpdateFunctions() throws IOException, InterruptedException, ExecutionException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		when(plc.getElevatorNum()).thenReturn(1);
		when(plc.getFloorNum()).thenReturn(2);
		when(plc.getFloorButtonUp(0)).thenReturn(false);
		when(plc.getFloorButtonDown(1)).thenReturn(false);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		PipedInputStream input = new PipedInputStream();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);

		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
				try {
					adapter.run(input, output);
				} catch (InterruptedException | IOException | ExecutionException e) {
					throw new IllegalArgumentException("exception");
				}
		    }
		});  
		t1.start();
		
		Thread.sleep(100);
		assertTrue(t1.isAlive());

		assertFalse(building.getFloors()[0].isButtonUp());
		assertFalse(building.getFloors()[1].isButtonDown());
		
		when(plc.getFloorButtonUp(0)).thenReturn(true);
		when(plc.getFloorButtonDown(1)).thenReturn(true);
		
		Thread.sleep(251);
		
		assertTrue(building.getFloors()[0].isButtonUp());
		assertTrue(building.getFloors()[1].isButtonDown());
		
		inWriter.write("exit\n");
		inWriter.flush();
		
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			t1.join();
	    });
		
		}
	}	
}
