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
