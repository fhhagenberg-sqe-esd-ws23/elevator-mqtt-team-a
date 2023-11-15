package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ElevatorUpdaterTest {

	@Test
	void testUpdateCapacity() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorCapacity(0)).thenReturn(5);		
		Elevator elevator = new Elevator(plc, 0);

		ElevatorUpdater updater = new ElevatorUpdater(elevator);
		when(plc.getElevatorCapacity(0)).thenReturn(11);
		updater.update();
		
		assertEquals(11, elevator.getCapacity());
	}

}
