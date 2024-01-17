package at.fhhagenberg.sqelevator.mqttadapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import at.fhhagenberg.sqelevator.ElevatorProperties;
import sqelevator.IElevator;

class MainTest {
	
	@Test
	void testStartWithElevatorSimulatorNullArgs() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(null, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}
	
	@Test
	void testStartWithElevatorSimulatorEmptyArgs() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[0];
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}

	@Test
	void testStartWithPlcMock() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[1];
		args[0] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using RMI API Mock."));
	}
	
	@Test
	void testStartWithPlcMockAndRunSomeTime() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[1];
		args[0] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Let the mqtt adapter run some time
			Thread.sleep(2500);
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using RMI API Mock."));
	}
	
	@Test
	void testStartWithPlcMockWrongArgumentNumber() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[2];
		args[1] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}
	
	@Test
	void testStartWithPlcMockWrongArgumentContent() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[2];
		args[0] = "rmimok";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(args, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Using elevator simulator."));
	}

	@Test
	void testConnectionLoss() throws IOException, InterruptedException, AlreadyBoundException, NotBoundException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65521);
		when(props.getRmiName()).thenReturn("IElevator");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Registry registry = LocateRegistry.createRegistry(props.getRmiPort());
		IElevator obj = new ElevatorPlcMock(2, 2, 3);
		IElevator stub = (IElevator) UnicastRemoteObject.exportObject(obj, 0);
        registry.bind("IElevator", stub); // Bind the remote object's stub in the registry
		
		try {			
			Main main = new Main();
			PipedInputStream input = new PipedInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PipedOutputStream out = new PipedOutputStream(input);
			String[] args = new String[0];
			
			try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
				
				Thread t1 = new Thread(new Runnable() {
				    @Override
				    public void run() {
						try {
							main.run(args, props, input, output);
						} catch (InterruptedException | IOException | ExecutionException e) {
							throw new IllegalArgumentException("exception");
						}
				    }
				});  
				t1.start();
				
				{
					boolean connected = false;
					for(int i = 0; i < 10; ++i) {
						if(output.toString().contains("Using elevator simulator.") && output.toString().contains("Connected to RMI API.")) {
							connected = true;
							break;
						}
						Thread.sleep(500);
					}						
					assertTrue(connected);
				}
				
				// "Disconnect"
				registry.unbind("IElevator");
				UnicastRemoteObject.unexportObject(obj, true); // unexport IElevator mock object		
				UnicastRemoteObject.unexportObject(registry, true); // close registry
				
				{
					boolean connectionLoss = false;
					for(int i = 0; i < 30; ++i) {
						if(output.toString().contains("Lost connection to RMI API")) {
							connectionLoss = true;
							break;
						}
						Thread.sleep(1000);
					}						
					assertTrue(connectionLoss);
				}
				
				inWriter.write("exit\n");  // exit application
				inWriter.flush();
	
				t1.join();
			}
		}
		finally {			
			// "Disconnect"
			try { registry.unbind("IElevator"); } catch(Exception e) {}
			try { UnicastRemoteObject.unexportObject(obj, true); } catch(Exception e) {} // unexport IElevator mock object
			try { UnicastRemoteObject.unexportObject(registry, true); } catch(Exception e) {} // close registry
			
		}
	}

	@Test
	void testReconnect() throws IOException, InterruptedException, AlreadyBoundException, NotBoundException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65520);
		when(props.getRmiName()).thenReturn("IElevator");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Registry registry = LocateRegistry.createRegistry(props.getRmiPort());
		IElevator obj = new ElevatorPlcMock(2, 2, 3);
		IElevator stub = (IElevator) UnicastRemoteObject.exportObject(obj, 0);
        registry.bind("IElevator", stub); // Bind the remote object's stub in the registry
		
		try {			
			Main main = new Main();
			PipedInputStream input = new PipedInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PipedOutputStream out = new PipedOutputStream(input);
			String[] args = new String[0];
			
			try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
				
				Thread t1 = new Thread(new Runnable() {
				    @Override
				    public void run() {
						try {
							main.run(args, props, input, output);
						} catch (InterruptedException | IOException | ExecutionException e) {
							throw new IllegalArgumentException("exception");
						}
				    }
				});  
				t1.start();
				
				{
					boolean connected = false;
					for(int i = 0; i < 10; ++i) {
						if(output.toString().contains("Using elevator simulator.") && output.toString().contains("Connected to RMI API.")) {
							connected = true;
							break;
						}
						Thread.sleep(500);
					}						
					assertTrue(connected);
				}
				
				// "Disconnect"
				registry.unbind("IElevator");
				UnicastRemoteObject.unexportObject(obj, true); // unexport IElevator mock object		
				UnicastRemoteObject.unexportObject(registry, true); // close registry
				
				{
					boolean connectionLoss = false;
					for(int i = 0; i < 30; ++i) {
						if(output.toString().contains("Lost connection to RMI API")) {
							connectionLoss = true;
							break;
						}
						Thread.sleep(1000);
					}						
					assertTrue(connectionLoss);
				}
				
				registry = LocateRegistry.createRegistry(props.getRmiPort());
				obj = new ElevatorPlcMock(2, 2, 3);
				stub = (IElevator) UnicastRemoteObject.exportObject(obj, 0);
		        registry.bind("IElevator", stub); // Bind the remote object's stub in the registry
		        
		        {
					boolean connected = false;
					for(int i = 0; i < 30; ++i) {
						if(output.toString().contains("Lost connection to RMI API")) {
							connected = true;
							break;
						}
						Thread.sleep(1000);
					}						
					assertTrue(connected);
				}
				
				inWriter.write("exit\n");  // exit application
				inWriter.flush();
	
				t1.join();
			}
		}
		finally {			
			// "Disconnect"
			try { registry.unbind("IElevator"); } catch(Exception e) {}
			try { UnicastRemoteObject.unexportObject(obj, true); } catch(Exception e) {} // unexport IElevator mock object
			try { UnicastRemoteObject.unexportObject(registry, true); } catch(Exception e) {} // close registry
			
		}
	}
	
	@Test
	void testExitMessageOnCorrectInput() throws IOException, InterruptedException {
		ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("unused");
		when(props.getMqttAddress()).thenReturn("broker.hivemq.com");
		when(props.getMqttPort()).thenReturn(1883);
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		Main main = new Main();
		PipedInputStream input = new PipedInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PipedOutputStream out = new PipedOutputStream(input);
		String[] args = new String[1];
		args[0] = "rmimock";
		
		try (OutputStreamWriter inWriter = new OutputStreamWriter(out)) {
			
			Thread t1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
					try {
						main.run(null, props, input, output);
					} catch (InterruptedException | IOException | ExecutionException e) {
						throw new IllegalArgumentException("exception");
					}
			    }
			});  
			t1.start();
			
			// Immediately exit
			inWriter.write("exit\n");
			inWriter.flush();
			
			t1.join();
		}
		
		String outputString = output.toString();
		assertTrue(outputString.contains("Exited on user request."));
	}

}
