package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ElevatorTest {
	
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

}
