package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ElevatorTest {

	@Test
	void testGetPlc() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(plc, elevator.getPlc());
	}

	@Test
	void testElevator_PlcIsNull() throws RemoteException {
		IElevator plc = null;

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Elevator(plc, 0));
		assertEquals("Plc must be valid!", thrown.getMessage());
	}

	@Test
	void testGetNumber_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getNumber());
	}

	@Test
	void testGetNumber_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Elevator(plc, -1));
		assertEquals("The number of the Elevator must be >=0!", thrown.getMessage());
	}

	@Test
	void testGetNumber_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 50);

		assertEquals(50, elevator.getNumber());
	}

	@Test
	void testGetCommitedDirection_Uncommited() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
	}

	@Test
	void testGetCommitedDirection_Down() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_DOWN);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator.getCommittedDirection());
	}

	@Test
	void testGetCommitedDirection_Up() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UP);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
	}

	@Test
	void testSetCommitedDirection_Uncommited() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);

		assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
	}

	@Test
	void testSetCommitedDirection_Up() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UP);

		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
	}

	@Test
	void testSetCommitedDirection_Down() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_DOWN);

		assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator.getCommittedDirection());
	}

	@Test
	void testSetCommitedDirection_Invalid() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setCommittedDirection(-1));
		assertEquals("The commited direction must be 0, 1 or2!", thrown.getMessage());
	}

	@Test
	void testCommitedDirection_Changed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_DOWN);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.COMMITTED_DIRECTION_PROPERTY_NAME
						&& (int) event.getOldValue() == IElevator.ELEVATOR_DIRECTION_UNCOMMITTED
						&& (int) event.getNewValue() == IElevator.ELEVATOR_DIRECTION_DOWN));
	}

	@Test
	void testGetAcceleration_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorAccel(0)).thenReturn(5);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(5, elevator.getAcceleration());
	}

	@Test
	void testGetAcceleration_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorAccel(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getAcceleration());
	}

	@Test
	void testGetAcceleration_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorAccel(0)).thenReturn(-2);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(-2, elevator.getAcceleration());
	}

	@Test
	void testSetAcceleration_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setAcceleration(3);

		assertEquals(3, elevator.getAcceleration());
	}

	@Test
	void testSetAcceleration_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setAcceleration(0);

		assertEquals(0, elevator.getAcceleration());
	}

	@Test
	void testSetAcceleration_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setAcceleration(-4);

		assertEquals(-4, elevator.getAcceleration());
	}

	@Test
	void testAcceleration_Changed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorAccel(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setAcceleration(5);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.ACCELERATION_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 5));
	}

	@Test
	void testGetStopRequest_NegativeFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.getStopRequest(-1));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testGetStopRequest_FloorOutOfRange() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(15);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.getStopRequest(15));

		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testGetStopRequest_NoStopRequest() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);

		assertFalse(elevator.getStopRequest(3));
	}

	@Test
	void testSetStopRequest_NegativeFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setStopRequest(-1, true));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testSetStopRequest_FloorOutOfRange() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(15);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setStopRequest(15, true));

		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testSetStopRequests_Valid() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setStopRequest(3, true);

		assertTrue(elevator.getStopRequest(3));
	}

//	@Test
//	void testStopRequestChanged() throws RemoteException {
//		IElevator plc = mock(IElevator.class);
//		when(plc.getFloorNum()).thenReturn(2);
//		PropertyChangeListener listener = mock(PropertyChangeListener.class);
//
//		Elevator elevator = new Elevator(plc, 0);
//		elevator.addPropertyChangeListener(listener);
//		elevator.setStopRequest(1, true);
//
//		verify(listener, times(1))
//				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.STOP_REQUESTS_PROPERTY_NAME
//						&& ((boolean[]) ((Object) event.getOldValue()))[0] == false
//						&& ((boolean[]) ((Object) event.getOldValue()))[1] == false
//						&& ((boolean[]) ((Object) event.getNewValue()))[0] == false
//						&& ((boolean[]) ((Object) event.getNewValue()))[1] == true));
//	}

	@Test
	void testElevator0Capacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(0)).thenReturn(5);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(5, elevator.getCapacity());
	}

	@Test
	void testElevator1Capacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(1)).thenReturn(2);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 1);

		assertEquals(2, elevator.getCapacity());
	}

	@Test
	void testCapacityChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(0)).thenReturn(4);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setCapacity(5);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.CAPACITY_PROPERTY_NAME
						&& (int) event.getOldValue() == 4 && (int) event.getNewValue() == 5));
	}

	@Test
	void testGetDoorStatus_Open() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_OPEN);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DOORS_OPEN, elevator.getDoorStatus());
	}

	@Test
	void testGetDoorStatus_Closed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DOORS_CLOSED, elevator.getDoorStatus());
	}

	@Test
	void testSetDoorStatus_Open() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);

		assertEquals(IElevator.ELEVATOR_DOORS_OPEN, elevator.getDoorStatus());
	}

	@Test
	void testSetDoorStatus_Closed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setDoorStatus(IElevator.ELEVATOR_DOORS_CLOSED);

		assertEquals(IElevator.ELEVATOR_DOORS_CLOSED, elevator.getDoorStatus());
	}

	@Test
	void testDoorStatusChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.DOOR_STATUS_PROPERTY_NAME
						&& (int) event.getOldValue() == IElevator.ELEVATOR_DOORS_CLOSED
						&& (int) event.getNewValue() == IElevator.ELEVATOR_DOORS_OPEN));
	}

	@Test
	void testGetFloor_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorFloor(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getFloor());
	}

	@Test
	void testGetFloor_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(8);
		when(plc.getElevatorFloor(0)).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(5, elevator.getFloor());
	}

	@Test
	void testSetFloor_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setFloor(0);

		assertEquals(0, elevator.getFloor());
	}

	@Test
	void testSetFloor_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(8);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setFloor(5);

		assertEquals(5, elevator.getFloor());
	}

	@Test
	void testSetFloor_FloorInvalid() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setFloor(5));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testSetFloor_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setFloor(-2));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testFloorChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorFloor(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setFloor(1);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.FLOOR_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 1));
	}

	@Test
	void testGetPosition_OnGroundLevel() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorPosition(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getPosition());
	}

	@Test
	void testGetPosition_AboveGroundLevel() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorPosition(0)).thenReturn(10);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(10, elevator.getPosition());
	}

	@Test
	void testSetPosition_OnGroundLevel() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setPosition(0);

		assertEquals(0, elevator.getPosition());
	}

	@Test
	void testSetPosition_AboveGroundLevel() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setPosition(5);

		assertEquals(5, elevator.getPosition());
	}

	@Test
	void testSetPosition_ToHigh() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setPosition(50));
		assertEquals("Invalid Position!", thrown.getMessage());
	}

	@Test
	void testSetPosition_BelowGroundLevel() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setPosition(-2));
		assertEquals("Invalid Position!", thrown.getMessage());
	}

	@Test
	void testPositionChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorPosition(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getFloorHeight()).thenReturn(3);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setPosition(5);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.POSITION_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 5));
	}

	@Test
	void testGetSpeed_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorSpeed(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(8);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getSpeed());
	}

	@Test
	void testGetSpeed_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorSpeed(0)).thenReturn(2);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(2, elevator.getSpeed());
	}

	@Test
	void testGetSpeed_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorSpeed(0)).thenReturn(-1);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(-1, elevator.getSpeed());
	}

	@Test
	void testSetSpeed_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setSpeed(0);

		assertEquals(0, elevator.getSpeed());
	}

	@Test
	void testSetSpeed_Positve() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setSpeed(5);

		assertEquals(5, elevator.getSpeed());
	}

	@Test
	void testSetSpeed_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setSpeed(-3);

		assertEquals(-3, elevator.getSpeed());
	}

	@Test
	void testSpeedChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorSpeed(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setSpeed(5);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.SPEED_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 5));
	}

	@Test
	void testGetWeight_NoPassengers() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorWeight(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getWeight());
	}

	@Test
	void testGetWeight_WithPassengers() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorWeight(0)).thenReturn(100);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(100, elevator.getWeight());
	}

	@Test
	void testSetWeight_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setWeight(0);

		assertEquals(0, elevator.getWeight());
	}

	@Test
	void testSetWeight_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setWeight(50);

		assertEquals(50, elevator.getWeight());
	}

	@Test
	void testSetWeight_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setWeight(-2));
		assertEquals("The weight can't be negative!", thrown.getMessage());
	}
	
	@Test
	void testWeightChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorWeight(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setWeight(30);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.WEIGHT_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 30));
	}

	@Test
	void testGetServicesFloor_NegativeFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.getServicesFloor(-2));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testGetServicesFloor_FloorOutOfRange() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.getServicesFloor(10));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testGetServicesFloor_NoService() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		assertFalse(elevator.getServicesFloor(5));
	}

	@Test
	void testSetServicesFloor_NegativeFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setServicesFloor(-1, true));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testSetServicesFloor_FloorOutOfRange() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(15);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setServicesFloor(15, true));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testSetServicesFloor_Valid() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setServicesFloor(3, true);

		assertTrue(elevator.getServicesFloor(3));
	}

//	@Test
//	void testServicedFloorsChanged() throws RemoteException {
//		IElevator plc = mock(IElevator.class);
//		when(plc.getFloorNum()).thenReturn(2);
//		when(plc.getServicesFloors(0, 1)).thenReturn(false);
//		PropertyChangeListener listener = mock(PropertyChangeListener.class);
//
//		Elevator elevator = new Elevator(plc, 0);
//		elevator.addPropertyChangeListener(listener);
//		elevator.setServicesFloor(1, true);
//
//		verify(listener, times(1))
//				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.STOP_REQUESTS_PROPERTY_NAME
//						&& ((boolean[]) ((Object) event.getOldValue()))[0] == false
//						&& ((boolean[]) ((Object) event.getOldValue()))[1] == false
//						&& ((boolean[]) ((Object) event.getNewValue()))[0] == false
//						&& ((boolean[]) ((Object) event.getNewValue()))[1] == true));
//	}

	@Test
	void testGetTarget_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getTarget(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(0, elevator.getTarget());
	}

	@Test
	void testGetTarget_PositiveTarget() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getTarget(0)).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);

		assertEquals(5, elevator.getTarget());
	}

	@Test
	void testSetTarget_Zero() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setTarget(0);

		assertEquals(0, elevator.getTarget());
	}

	@Test
	void testSetTarget_Positive() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(8);

		Elevator elevator = new Elevator(plc, 0);
		elevator.setTarget(5);

		assertEquals(5, elevator.getTarget());
	}

	@Test
	void testSetTarget_FloorInvalid() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setTarget(5));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testSetTarget_Negative() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);

		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> elevator.setTarget(-2));
		assertEquals("Invalid floor", thrown.getMessage());
	}

	@Test
	void testTargetChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getTarget(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setTarget(1);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.TARGET_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 1));
	}

	@Test
	void testRemovePropertyChangedListener() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getTarget(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setTarget(1);
		elevator.removePropertyChangeListener(listener);
		elevator.setTarget(2);

		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.TARGET_PROPERTY_NAME
						&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 1));
	}
	
	@Test
	void testElevatorAddPropertyChangeListenerTwoTimes_thenRemoveOneAfterFirstEvent() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getTarget(0)).thenReturn(0);
		when(plc.getFloorNum()).thenReturn(10);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);

		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.addPropertyChangeListener(listener);
		
		elevator.setTarget(1);
		verify(listener, times(2))
		.propertyChange(argThat(event -> event.getPropertyName() == Elevator.TARGET_PROPERTY_NAME
				&& (int) event.getOldValue() == 0 && (int) event.getNewValue() == 1));
		
		elevator.removePropertyChangeListener(listener);
		elevator.setTarget(2);
		verify(listener, times(1))
				.propertyChange(argThat(event -> event.getPropertyName() == Elevator.TARGET_PROPERTY_NAME
						&& (int) event.getOldValue() == 1 && (int) event.getNewValue() == 2));
	}
	
	@Test
	void testGetStopRequest_SomeStops() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);
		when(plc.getElevatorButton(0, 0)).thenReturn(true);
		
		Elevator elevator = new Elevator(plc, 0);
		
		assertTrue(elevator.getStopRequest(0));
	}
	
	@Test
	void testGetServicesFloor_SomeServices() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(5);
		when(plc.getServicesFloors(0, 0)).thenReturn(true);
		
		Elevator elevator = new Elevator(plc, 0);
		
		assertTrue(elevator.getServicesFloor(0));
	}
}
