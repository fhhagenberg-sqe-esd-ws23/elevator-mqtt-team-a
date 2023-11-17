package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;

class BuildingTest {

	@Test
	void testNumberOfElevators() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(4);
		
		Building building = new Building(plc);
		
		assertEquals(4, building.getElevators().length);
	}
	
	@Test
	void testNumberOfUpdaters() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(4);
		when(plc.getFloorNum()).thenReturn(5);
		
		Building building = new Building(plc);
		
		assertEquals(9, building.getUpdaters().length);
	}
	
	@Test
	void testNumberOffFloors() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(7);
		
		Building building = new Building(plc);
		
		assertEquals(7, building.getFloors().length);
	}

}
