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
	
	
	@Test
	void testBuildingCreateWithNullPlc() throws RemoteException
	{
		IElevator plc = null;
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()->new Building(plc));		
		assertEquals("Plc must be valid!", thrown.getMessage());
	}
	
	
	@Test
	void testBuildingUpdate_withFloorButtonDown() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(1);
		when(plc.getFloorNum()).thenReturn(1);
		
		Building building = new Building(plc);
		
		
		assertFalse(building.getFloors()[0].isButtonDown());
		
		when(plc.getFloorButtonDown(0)).thenReturn(true);
		
		assertFalse(building.getFloors()[0].isButtonDown());
		
		building.update();
		
		assertTrue(building.getFloors()[0].isButtonDown());
	}
	
	@Test
	void testBuildingUpdate_withElevatorDoorStatus() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(1);
		when(plc.getFloorNum()).thenReturn(1);
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
		
		Building building = new Building(plc);
		
		
		assertEquals(IElevator.ELEVATOR_DOORS_CLOSED,building.getElevators()[0].getDoorStatus());
		
		when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_OPEN);
		
		assertEquals(IElevator.ELEVATOR_DOORS_CLOSED,building.getElevators()[0].getDoorStatus());
		
		building.update();
		
		assertEquals(IElevator.ELEVATOR_DOORS_OPEN,building.getElevators()[0].getDoorStatus());
		
	}

}
