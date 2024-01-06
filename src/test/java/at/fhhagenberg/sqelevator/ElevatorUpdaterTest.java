package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sqelevator.IElevator;

@ExtendWith(MockitoExtension.class)
class ElevatorUpdaterTest {
	@Test
	void testUpdateCommitedDirection() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UP);
		updater.update();
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
	}
	
	@Test
	void testUpdateAcceleration() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorAccel(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorAccel(0)).thenReturn(10);
		updater.update();
		
		assertEquals(10, elevator.getAcceleration());
	}
	
	@Test
	void testUpdateStopRequests() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorButton(0, 0)).thenReturn(false);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorButton(0, 0)).thenReturn(true);
		updater.update();
		
		assertTrue(elevator.getStopRequest(0));
	}

	@Test
	void testUpdateCapacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorCapacity(0)).thenReturn(5);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorCapacity(0)).thenReturn(10);
		updater.update();
		
		assertEquals(10, elevator.getCapacity());
	}
	
	@Test
	void testUpdateDoorStatus() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_OPEN);
		updater.update();
		
		assertEquals(IElevator.ELEVATOR_DOORS_OPEN, elevator.getDoorStatus());
	}
	
	@Test
	void testUpdateFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorFloor(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorFloor(0)).thenReturn(1);
		updater.update();
		
		assertEquals(1, elevator.getFloor());
	}
	
	@Test
	void testUpdatePosition() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorPosition(0)).thenReturn(0);
		when(plc.getFloorHeight()).thenReturn(3);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorPosition(0)).thenReturn(1);
		updater.update();
		
		assertEquals(1, elevator.getPosition());
	}
	
	@Test
	void testUpdateSpeed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorSpeed(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorSpeed(0)).thenReturn(1);
		updater.update();
		
		assertEquals(1, elevator.getSpeed());
	}
	
	@Test
	void testUpdateWeight() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getElevatorWeight(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorWeight(0)).thenReturn(1);
		updater.update();
		
		assertEquals(1, elevator.getWeight());
	}
	
	@Test
	void testUpdateServicesFloor() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getServicesFloors(0, 0)).thenReturn(false);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getServicesFloors(0, 0)).thenReturn(true);
		updater.update();
		
		assertTrue(elevator.getServicesFloor(0));
	}
	
	@Test
	void testUpdateTarget() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(10);
		when(plc.getTarget(0)).thenReturn(0);

		Elevator elevator = new Elevator(plc, 0);
		
		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getTarget(0)).thenReturn(1);
		updater.update();
		
		assertEquals(1, elevator.getTarget());
	}
}
