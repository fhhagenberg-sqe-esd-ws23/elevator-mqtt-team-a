package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;

class ElevatorsMqttAdapterTest {

	@Test
	void testNumberOfElevators() throws RemoteException {
		IElevator plc = mock(IElevator.class);
		when(plc.getElevatorNum()).thenReturn(4);
		
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(plc);
		
		assertEquals(4, adapter.getElevators().length);

	}

}
