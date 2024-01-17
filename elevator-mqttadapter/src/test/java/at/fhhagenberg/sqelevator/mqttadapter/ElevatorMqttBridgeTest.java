package at.fhhagenberg.sqelevator.mqttadapter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import at.fhhagenberg.sqelevator.Elevator;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;

@ExtendWith(MockitoExtension.class)
class ElevatorMqttBridgeTest {
	private ElevatorsMqttClient mqtt;
	private Elevator elevator;
	private ElevatorMqttBridge bridge;
	
	@BeforeEach
	void setup() {
		mqtt = mock(ElevatorsMqttClient.class);
		elevator = mock(Elevator.class);
		
		bridge = new ElevatorMqttBridge(elevator, mqtt);
	}
	
	@Test
	void testStart() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getCommittedDirection()).thenReturn(0);
		when(elevator.getAcceleration()).thenReturn(0);
		when(elevator.getNumberOfFloors()).thenReturn(2);
		when(elevator.getStopRequest(0)).thenReturn(false);
		when(elevator.getStopRequest(1)).thenReturn(true);
		when(elevator.getCapacity()).thenReturn(5);
		when(elevator.getDoorStatus()).thenReturn(0);
		when(elevator.getFloor()).thenReturn(0);
		when(elevator.getPosition()).thenReturn(0);
		when(elevator.getSpeed()).thenReturn(0);
		when(elevator.getWeight()).thenReturn(100);
		when(elevator.getServicesFloor(0)).thenReturn(true);
		when(elevator.getServicesFloor(1)).thenReturn(true);
		when(elevator.getTarget()).thenReturn(1);
		
		bridge.start();
		
		verify(elevator, times(1)).addPropertyChangeListener(bridge);
		verify(mqtt, times(1)).publishDirection(0, 0);
		verify(mqtt, times(1)).publishAcceleration(0, 0);
		verify(mqtt, times(1)).publishButtonPressed(0, 0, false);
		verify(mqtt, times(1)).publishButtonPressed(0, 1, true);
		verify(mqtt, times(1)).publishCapacity(0, 5);
		verify(mqtt, times(1)).publishDoors(0, 0);
		verify(mqtt, times(1)).publishFloor(0, 0);
		verify(mqtt, times(1)).publishPosition(0, 0);
		verify(mqtt, times(1)).publishSpeed(0, 0);
		verify(mqtt, times(1)).publishWeight(0, 100);
		verify(mqtt, times(1)).publishServicesFloor(0, 0, true);
		verify(mqtt, times(1)).publishServicesFloor(0, 1, true);
		verify(mqtt, times(1)).publishTarget(0, 1);
	}
	
	@Test
	void testStartTwice() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getCommittedDirection()).thenReturn(0);
		when(elevator.getAcceleration()).thenReturn(0);
		when(elevator.getNumberOfFloors()).thenReturn(0);
		when(elevator.getCapacity()).thenReturn(5);
		when(elevator.getDoorStatus()).thenReturn(0);
		when(elevator.getFloor()).thenReturn(0);
		when(elevator.getPosition()).thenReturn(0);
		when(elevator.getSpeed()).thenReturn(0);
		when(elevator.getWeight()).thenReturn(100);
		when(elevator.getTarget()).thenReturn(1);
		
		bridge.start();
		bridge.start();
		
		verify(elevator, times(1)).addPropertyChangeListener(bridge);
		verify(mqtt, times(1)).publishDirection(0, 0);
		verify(mqtt, times(1)).publishAcceleration(0, 0);
		verify(mqtt, times(0)).publishButtonPressed(0, 0, false);
		verify(mqtt, times(0)).publishButtonPressed(0, 1, true);
		verify(mqtt, times(1)).publishCapacity(0, 5);
		verify(mqtt, times(1)).publishDoors(0, 0);
		verify(mqtt, times(1)).publishFloor(0, 0);
		verify(mqtt, times(1)).publishPosition(0, 0);
		verify(mqtt, times(1)).publishSpeed(0, 0);
		verify(mqtt, times(1)).publishWeight(0, 100);
		verify(mqtt, times(0)).publishServicesFloor(0, 0, false);
		verify(mqtt, times(0)).publishServicesFloor(0, 1, false);
		verify(mqtt, times(1)).publishTarget(0, 1);
	}
	
	@Test
	void testStop() {
		bridge.stop();
		
		verify(elevator, times(1)).removePropertyChangeListener(bridge);
	}
	
	@Test
	void testPropertyChange_CommitedDirection() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getCommittedDirection()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.COMMITTED_DIRECTION_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishDirection(0, 0);
		
	}
	
	@Test
	void testPropertyChange_Acceleratrion() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getAcceleration()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.ACCELERATION_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishAcceleration(0, 0);
	}
	
	@Test
	void testPropertyChange_StopRequests() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getNumberOfFloors()).thenReturn(2);
		when(elevator.getStopRequest(0)).thenReturn(false);
		when(elevator.getStopRequest(1)).thenReturn(true);
	
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.STOP_REQUESTS_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishButtonPressed(0, 0, false);
		verify(mqtt, times(1)).publishButtonPressed(0, 1, true);
	}
	
	@Test
	void testPropertyChange_Capacity() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getCapacity()).thenReturn(5);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.CAPACITY_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishCapacity(0, 5);
	}
	
	@Test
	void testPropertyChange_DoorStatus() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getDoorStatus()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.DOOR_STATUS_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishDoors(0, 0);
	}
	
	@Test
	void testPropertyChange_Floor() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getFloor()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.FLOOR_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishFloor(0, 0);
	}
	
	@Test
	void testPropertyChange_Position() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getPosition()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.POSITION_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishPosition(0, 0);
	}
	
	@Test
	void testPropertyChange_Speed() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getSpeed()).thenReturn(0);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.SPEED_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishSpeed(0, 0);
	}
	
	@Test
	void testPropertyChange_Weight() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getWeight()).thenReturn(100);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.WEIGHT_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishWeight(0, 100);
	}
	
	@Test
	void testPropertyChange_ServicedFloors() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getNumberOfFloors()).thenReturn(2);
		when(elevator.getServicesFloor(0)).thenReturn(true);
		when(elevator.getServicesFloor(1)).thenReturn(true);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.SERVICED_FLOORS_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishServicesFloor(0, 0, true);
		verify(mqtt, times(1)).publishServicesFloor(0, 1, true);
	}
	
	@Test
	void testPropertyChange_Target() {
		when(elevator.getNumber()).thenReturn(0);
		when(elevator.getTarget()).thenReturn(1);
		
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, Elevator.TARGET_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishTarget(0, 1);
	}
	
	@Test
	void testPropertyChange_InvalidPropertySource() {
		Object invalid = new Object();
		PropertyChangeEvent event = new PropertyChangeEvent(invalid, Elevator.TARGET_PROPERTY_NAME, null, null);
		bridge.propertyChange(event);
		
		verify(elevator, times(0)).addPropertyChangeListener(bridge);
		verify(mqtt, times(0)).publishDirection(0, 0);
		verify(mqtt, times(0)).publishAcceleration(0, 0);
		verify(mqtt, times(0)).publishButtonPressed(0, 0, false);
		verify(mqtt, times(0)).publishButtonPressed(0, 1, true);
		verify(mqtt, times(0)).publishCapacity(0, 5);
		verify(mqtt, times(0)).publishDoors(0, 0);
		verify(mqtt, times(0)).publishFloor(0, 0);
		verify(mqtt, times(0)).publishPosition(0, 0);
		verify(mqtt, times(0)).publishSpeed(0, 0);
		verify(mqtt, times(0)).publishWeight(0, 100);
		verify(mqtt, times(0)).publishServicesFloor(0, 0, true);
		verify(mqtt, times(0)).publishServicesFloor(0, 1, true);
		verify(mqtt, times(0)).publishTarget(0, 1);
	}

	@Test
	void testPropertyChange_InvalidPropertyName() {
		PropertyChangeEvent event = new PropertyChangeEvent(elevator, "invalid", null, null);
		
		RuntimeException ex = assertThrowsExactly( RuntimeException.class, ()-> bridge.propertyChange(event));	
		assertEquals("Unknown property name.",ex.getMessage());
		
		verify(elevator, times(0)).addPropertyChangeListener(bridge);
		verify(mqtt, times(0)).publishDirection(0, 0);
		verify(mqtt, times(0)).publishAcceleration(0, 0);
		verify(mqtt, times(0)).publishButtonPressed(0, 0, false);
		verify(mqtt, times(0)).publishButtonPressed(0, 1, true);
		verify(mqtt, times(0)).publishCapacity(0, 5);
		verify(mqtt, times(0)).publishDoors(0, 0);
		verify(mqtt, times(0)).publishFloor(0, 0);
		verify(mqtt, times(0)).publishPosition(0, 0);
		verify(mqtt, times(0)).publishSpeed(0, 0);
		verify(mqtt, times(0)).publishWeight(0, 100);
		verify(mqtt, times(0)).publishServicesFloor(0, 0, true);
		verify(mqtt, times(0)).publishServicesFloor(0, 1, true);
		verify(mqtt, times(0)).publishTarget(0, 1);
	}
	
	@Test
	void testSetCommitedDirection() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);

		bridge.start();
		bridge.setCommittedDirection(0, 0);
		
		verify(elevator, times(1)).setCommittedDirection(0);
	}
	
	@Test
	void testSetCommitedDirection_FalseElevator() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		
		bridge.start();
		bridge.setCommittedDirection(1, 0);
		
		verify(elevator, times(0)).setCommittedDirection(0);
	}
	
	@Test
	void testSetCommitedDirection_ThrowsException() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		doThrow(new RemoteException()).when(elevator).setCommittedDirection(0);

		bridge.start();
		bridge.setCommittedDirection(0, 0);
		
		verify(elevator, times(1)).setCommittedDirection(0);
	}
	
	@Test
	void testSetServicesFloor() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);

		bridge.start();
		bridge.setServicesFloor(0, 0, false);
		
		verify(elevator, times(1)).setServicesFloor(0, false);
	}
	
	@Test
	void testSetServicesFloor_FalseElevator() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		
		bridge.start();
		bridge.setServicesFloor(1, 0, false);
		
		verify(elevator, times(0)).setServicesFloor(0, false);
	}
	
	@Test
	void testSetServicesFloor_ThrowsException() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		doThrow(new RemoteException()).when(elevator).setServicesFloor(0, false);

		bridge.start();
		bridge.setServicesFloor(0, 0, false);
		
		verify(elevator, times(1)).setServicesFloor(0, false);
	}
	
	@Test
	void testSetTarget() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);

		bridge.start();
		bridge.setTarget(0, 0);
		
		verify(elevator, times(1)).setTarget(0);
	}
	
	@Test
	void testSetTarget_FalseElevator() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		
		bridge.start();
		bridge.setTarget(1, 0);
		
		verify(elevator, times(0)).setTarget(0);
	}
	
	@Test
	void testSetTarget_ThrowsException() throws RemoteException {
		when(elevator.getNumber()).thenReturn(0);
		doThrow(new RemoteException()).when(elevator).setTarget(0);

		bridge.start();
		bridge.setTarget(0, 0);
		
		verify(elevator, times(1)).setTarget(0);
	}
}
