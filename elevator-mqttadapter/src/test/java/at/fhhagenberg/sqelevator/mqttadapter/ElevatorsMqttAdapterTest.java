package at.fhhagenberg.sqelevator.mqttadapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.ExitCommandThread;
import sqelevator.IElevator;

class ElevatorsMqttAdapterTest {

	@Test
	void testNumberOfUpdaters() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		when(plc.getElevatorNum()).thenReturn(4);
		when(plc.getFloorNum()).thenReturn(5);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(1)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(2)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(3)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		Building building = new Building(plc);
		
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals(9, adapter.getUpdaters().length);
	}

	@Test
	void testNumberOfBridges() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		when(plc.getElevatorNum()).thenReturn(3);
		when(plc.getFloorNum()).thenReturn(4);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(1)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(2)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getElevatorDoorStatus(3)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		Building building = new Building(plc);
		
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		
		assertEquals(7, adapter.getBridges().length);
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
	void testExitOnRightInput() throws IOException, InterruptedException, ExecutionException {
		IElevator plc = mock(IElevator.class);
		ElevatorsMqttClient mqtt = mock(ElevatorsMqttClient.class);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);		
		
		ExitCommandThread exitThread = new ExitCommandThread(input, "exit");
		exitThread.start();
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						adapter.run(exitThread, output);
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
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mqtt.subscribeToControlMessages(1,2)).thenReturn(true);
 
		
		
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		PipedInputStream input = new PipedInputStream();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		
		ExitCommandThread exitThread = new ExitCommandThread(input, "exit");
		exitThread.start();

		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
				try {
					adapter.run(exitThread, output);
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
