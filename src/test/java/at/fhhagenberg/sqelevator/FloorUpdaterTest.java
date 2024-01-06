package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sqelevator.IElevator;

@ExtendWith(MockitoExtension.class)
class FloorUpdaterTest {

	@Test
	void testUpdateSetButtonDownPressed() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonDown(0)).thenReturn(true);	
		
		assertFalse(floor.isButtonDown());
		
		updater.update();
		
		assertTrue(floor.isButtonDown());
	}
	
	@Test
	void testUpdateSetButtonUpPressed() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonUp(0)).thenReturn(true);	
		
		assertFalse(floor.isButtonUp());
		
		updater.update();
		
		assertTrue(floor.isButtonUp());
	}
	
	@Test
	void testUpdateSetButtonUpNoChange() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonUp(0)).thenReturn(false);	
		
		assertFalse(floor.isButtonUp());
		
		updater.update();
		
		assertFalse(floor.isButtonUp());
	}
	
	@Test
	void testUpdateSetButtonDownNoChange() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonDown(0)).thenReturn(false);	
		
		assertFalse(floor.isButtonDown());
		
		updater.update();
		
		assertFalse(floor.isButtonDown());
	}
	
	@Test
	void testUpdateSetButtonDownAndUpChange() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonDown(0)).thenReturn(true);
		when(plc.getFloorButtonUp(0)).thenReturn(true);	
		
		assertFalse(floor.isButtonDown());
		assertFalse(floor.isButtonUp());
		
		updater.update();
		
		assertTrue(floor.isButtonDown());
		assertTrue(floor.isButtonUp());
	}
	
	@Test
	void testUpdateSetButtonDownPressed_ButtonUpNoChange() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonDown(0)).thenReturn(true);	
		when(plc.getFloorButtonUp(0)).thenReturn(false);
		
		assertFalse(floor.isButtonDown());
		assertFalse(floor.isButtonUp());
		
		updater.update();
		
		assertTrue(floor.isButtonDown());
		assertFalse(floor.isButtonUp());
	}
	
	@Test
	void testUpdateSetButtonUpPressed_ButtonDownNoChange() throws RemoteException {
		IElevator plc = mock(IElevator.class);	
		Floor floor = new Floor(plc, 0);
		FloorUpdater updater = new FloorUpdater(floor);
		
		when(plc.getFloorButtonDown(0)).thenReturn(false);
		when(plc.getFloorButtonUp(0)).thenReturn(true);
		
		assertFalse(floor.isButtonDown());
		assertFalse(floor.isButtonUp());
		
		updater.update();
		
		assertFalse(floor.isButtonDown());
		assertTrue(floor.isButtonUp());
	}
	
	@Test
	void testFloorUpdaterCreateWithNullFloor() throws RemoteException
	{
		Floor floor = null;
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()->new FloorUpdater(floor));		
		assertEquals("Floor must be valid!", thrown.getMessage());
	}

}

