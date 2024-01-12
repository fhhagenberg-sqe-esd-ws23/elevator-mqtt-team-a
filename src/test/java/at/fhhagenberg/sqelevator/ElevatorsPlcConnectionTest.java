package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.junit.jupiter.api.Test;

import sqelevator.IElevator;

class ElevatorsPlcConnectionTest {

	@Test
	void testConnectMissingRegistry() {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("none");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ElevatorsPlcConnection conn = new ElevatorsPlcConnection(props);
		
		assertFalse(conn.connect(output));
		String outputString = output.toString();
		assertTrue(outputString.contains("Error during registry lookup"));
	}
	
	@Test
	void testConnectSuccessful() throws RemoteException, AlreadyBoundException, NotBoundException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("IElevator");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ElevatorsPlcConnection conn = new ElevatorsPlcConnection(props);
		
		Registry registry = LocateRegistry.createRegistry(props.getRmiPort());
		IElevator obj = mock(IElevator.class);
		IElevator stub = (IElevator) UnicastRemoteObject.exportObject(obj, 0);
        // Bind the remote object's stub in the registry
        registry.bind("IElevator", stub);
		
		assertTrue(conn.connect(output));
		String outputString = output.toString();
		assertTrue(outputString.isBlank());
		
		// Cleanup
		registry.unbind("IElevator");
		UnicastRemoteObject.unexportObject(obj, true); // unexport IElevator mock object
		UnicastRemoteObject.unexportObject(registry, true); // close registry
	}
	
	@Test
	void testConnectNotBound() throws RemoteException, AlreadyBoundException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("IElevator");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ElevatorsPlcConnection conn = new ElevatorsPlcConnection(props);
		
		Registry registry = LocateRegistry.createRegistry(props.getRmiPort());
		
		assertFalse(conn.connect(output));
		String outputString = output.toString();
		assertTrue(outputString.contains("Name not found during registry lookup"));
		
		// Cleanup
		UnicastRemoteObject.unexportObject(registry, true); // close registry
	}
	
	@Test
	void testCallFunctionBeforeConnect() {
		
	}

}
