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
		when(plc.getFloorNum()).thenReturn(1);
		
		Building building = new Building(plc);
		
		assertEquals(4, building.getElevators().length);
	}
	
	@Test
	void testNumberOffFloors() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorNum()).thenReturn(7);
		
		Building building = new Building(plc);
		
		assertEquals(7, building.getFloors().length);
	}
	
	
	@Test
	void testBuildingCreateWithNullPlc() throws RemoteException
	{
		IElevator plc = null;
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()->new Building(plc));		
		assertEquals("Plc must be valid!", thrown.getMessage());
	}

}
