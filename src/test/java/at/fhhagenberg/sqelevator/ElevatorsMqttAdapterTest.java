package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;

class ElevatorsMqttAdapterTest {

	@Test
	void testNumberOfUpdaters() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(4);
		when(plc.getFloorNum()).thenReturn(5);
		Building building = new Building(plc);
		
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building);
		
		assertEquals(9, adapter.getUpdaters().length);
	}

}
