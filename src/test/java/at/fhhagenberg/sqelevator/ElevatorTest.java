package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ElevatorTest {
	
	@Test
	void testGetPlc() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);
		
		assertEquals(plc, elevator.getPlc());
	}
	
	@Test
	void testGetNumber_Zero() throws RemoteException 
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);
		
		assertEquals(0, elevator.getNumber());
	}
	
	@Test
	void testGetNumber_Negative() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new Elevator(plc, -1));
		
		assertEquals("The number of the Elevator must be >=0!", thrown.getMessage());
	}
	
	@Test
	void testGetNumber_Positive() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 50);
		
		assertEquals(50, elevator.getNumber());
	}
	
	@Test
	void testGetCommitedDirection_Uncommited() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
		
		Elevator elevator = new Elevator(plc, 0);
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
	}
	
	@Test
	void testGetCommitedDirection_Down() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_DOWN);
		
		Elevator elevator = new Elevator(plc, 0);

		assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator.getCommittedDirection());
	}
	
	@Test
	void testGetCommitedDirection_Up() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getCommittedDirection(0)).thenReturn(IElevator.ELEVATOR_DIRECTION_UP);
		
		Elevator elevator = new Elevator(plc, 0);
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
	}
	
	@Test
	void testSetCommitedDirection_Uncommited() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);
		
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
	}
	
	@Test
	void testSetCommitedDirection_Up() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);
		
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UP);
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
	}
	
	@Test
	void testSetCommitedDirection_Down() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);
		
		elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_DOWN);
		
		assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator.getCommittedDirection());
	}
	
	@Test
	void testSetCommitedDirection_Invalid() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Elevator elevator = new Elevator(plc, 0);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> elevator.setCommittedDirection(-1));

		assertEquals("The commited direction must be 0, 1 or2!", thrown.getMessage());
	}
	
	@Test
	void testCommitedDirection_Changed() throws RemoteException {
		IElevator plc = mock(IElevator.class);
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
	void testElevator0Capacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(0)).thenReturn(5);
		
		Elevator elevator = new Elevator(plc, 0);
		
		assertEquals(5, elevator.getCapacity());
	}
	
	@Test
	void testElevator1Capacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(1)).thenReturn(2);
		
		Elevator elevator = new Elevator(plc, 1);
		
		assertEquals(2, elevator.getCapacity());
	}
	
	@Test
	void testCapacityChanged() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(0)).thenReturn(4);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		
		Elevator elevator = new Elevator(plc, 0);
		elevator.addPropertyChangeListener(listener);
		elevator.setCapacity(5);
		
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Elevator.CAPACITY_PROPERTY_NAME && (int)event.getOldValue() == 4 && (int)event.getNewValue() == 5));
	}

}
