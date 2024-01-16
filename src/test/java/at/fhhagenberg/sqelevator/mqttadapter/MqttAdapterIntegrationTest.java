package at.fhhagenberg.sqelevator.mqttadapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.datatypes.MqttUtf8String;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import org.junit.jupiter.api.Test;

import at.fhhagenberg.sqelevator.ElevatorProperties;
import at.fhhagenberg.sqelevator.MqttTopicGenerator;
import sqelevator.IElevator;

@Testcontainers
public class MqttAdapterIntegrationTest {
	private final MqttTopicGenerator topics = new MqttTopicGenerator();

    @Container
    HiveMQContainer container = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));

    private Mqtt3BlockingClient testClient;
    private ElevatorProperties properties;
    private Registry registry;
    private IElevator plc;
    private Main main;
    private PipedInputStream input;
    private ByteArrayOutputStream output;
    private PipedOutputStream inputWriteStream;
    private OutputStreamWriter inputWriter;
    private String[] args = new String[0];
    private Thread runThread;

	@BeforeEach
    void setUp() throws InterruptedException, ExecutionException, AlreadyBoundException, IOException {
        testClient = Mqtt3Client.builder().serverPort(container.getMqttPort()).buildBlocking();
        testClient.connect();
        
        properties = createPropertiesMock();			
        registry = LocateRegistry.createRegistry(properties.getRmiPort());
		plc = createPlcMock();
		
		IElevator stub = (IElevator) UnicastRemoteObject.exportObject(plc, properties.getRmiPort());
        registry.bind("IElevator", stub); // Bind the remote object's stub in the registry
        
        main = new Main();
		input = new PipedInputStream();
		output = new ByteArrayOutputStream();
		inputWriteStream = new PipedOutputStream(input);
		inputWriter = new OutputStreamWriter(inputWriteStream);

		runThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
				try {
					main.run(args, properties, input, output);
				} catch (InterruptedException | IOException | ExecutionException e) {
					throw new IllegalArgumentException("exception");
				}
		    }
		});
    }

    @AfterEach
    void cleanUp() throws InterruptedException, ExecutionException, IOException {
		inputWriter.write("exit\n");  // exit application
		inputWriter.flush();
		
		runThread.join();
		
    	testClient.disconnect();
    	
    	try { registry.unbind("IElevator"); } catch(Exception e) {}
		try { UnicastRemoteObject.unexportObject(plc, true); } catch(Exception e) {} // unexport IElevator mock object
		try { UnicastRemoteObject.unexportObject(registry, true); } catch(Exception e) {} // close registry
    }

    private ElevatorProperties createPropertiesMock() {
    	ElevatorProperties props = mock(ElevatorProperties.class);
		when(props.getRmiAddress()).thenReturn("localhost");
		when(props.getRmiPort()).thenReturn(65535);
		when(props.getRmiName()).thenReturn("IElevator");
		when(props.getMqttAddress()).thenReturn(container.getHost());
		when(props.getMqttPort()).thenReturn(container.getMqttPort());
		when(props.getRmiPollingInterval()).thenReturn(250);
		when(props.getExitLine()).thenReturn("exit");
		return props;
    }

    private IElevator createPlcMock() throws RemoteException {
    	IElevator obj = mock(IElevator.class);
		when(obj.getElevatorNum()).thenReturn(2);
		when(obj.getFloorNum()).thenReturn(3);
		when(obj.getFloorHeight()).thenReturn(4);
		when(obj.getCommittedDirection(0)).thenReturn(2);
		when(obj.getCommittedDirection(1)).thenReturn(0);
		when(obj.getElevatorAccel(0)).thenReturn(7);
		when(obj.getElevatorAccel(1)).thenReturn(8);
		when(obj.getElevatorButton(0, 0)).thenReturn(true);
		when(obj.getElevatorButton(0, 1)).thenReturn(false);
		when(obj.getElevatorButton(0, 2)).thenReturn(true);
		when(obj.getElevatorButton(1, 0)).thenReturn(false);
		when(obj.getElevatorButton(1, 1)).thenReturn(true);
		when(obj.getElevatorButton(1, 2)).thenReturn(false);
		when(obj.getElevatorCapacity(0)).thenReturn(9);
		when(obj.getElevatorCapacity(1)).thenReturn(10);
		when(obj.getElevatorDoorStatus(0)).thenReturn(1);
		when(obj.getElevatorDoorStatus(1)).thenReturn(2);
		when(obj.getElevatorFloor(0)).thenReturn(1);
		when(obj.getElevatorFloor(1)).thenReturn(0);
		when(obj.getElevatorPosition(0)).thenReturn(0);
		when(obj.getElevatorPosition(1)).thenReturn(2);
		when(obj.getElevatorSpeed(0)).thenReturn(11);
		when(obj.getElevatorSpeed(1)).thenReturn(12);
		when(obj.getElevatorWeight(0)).thenReturn(13);
		when(obj.getElevatorWeight(1)).thenReturn(14);
		when(obj.getTarget(0)).thenReturn(0);
		when(obj.getTarget(1)).thenReturn(1);
		when(obj.getServicesFloors(0, 0)).thenReturn(false);
		when(obj.getServicesFloors(0, 1)).thenReturn(true);
		when(obj.getServicesFloors(0, 2)).thenReturn(false);
		when(obj.getServicesFloors(1, 0)).thenReturn(true);
		when(obj.getServicesFloors(1, 1)).thenReturn(false);
		when(obj.getServicesFloors(1, 2)).thenReturn(true);
		when(obj.getFloorButtonDown(0)).thenReturn(true);
		when(obj.getFloorButtonDown(1)).thenReturn(false);
		when(obj.getFloorButtonDown(2)).thenReturn(true);
		when(obj.getFloorButtonUp(0)).thenReturn(false);
		when(obj.getFloorButtonUp(1)).thenReturn(true);
		when(obj.getFloorButtonUp(2)).thenReturn(false);
		return obj;
    }

    private void waitUntilAssertsTrue(BooleanSupplier func) throws InterruptedException {
    	waitUntilAssertsTrue(func, 10, 500);
    }

    private void waitUntilAssertsTrue(BooleanSupplier func, int tryTimes, int waitBetweenTriesMs) throws InterruptedException {    	
    	boolean result = false;
		for(int i = 0; i < tryTimes; ++i) {
			if(func.getAsBoolean()) {
				result = true;
				break;
			}
			Thread.sleep(waitBetweenTriesMs);
		}						
		assertTrue(result);
    }

    @Test
    void testConnectedStatusMessage() throws InterruptedException, AlreadyBoundException, IOException, NotBoundException {
    	testClient.subscribeWith().topicFilter(topics.getConnectedTopic()).qos(MqttQos.EXACTLY_ONCE).send();        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);
        
        runThread.start();
			
		Mqtt3Publish received = incoming.receive();
        assertTrue(received.getPayload().isPresent());
        assertEquals(0, received.getPayload().get().asIntBuffer().get());
        
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );
		
        received = incoming.receive();
        assertTrue(received.getPayload().isPresent());
        assertEquals(1, received.getPayload().get().asIntBuffer().get());
    }

    @Test
    void testConnectionLostStatusMessage() throws AlreadyBoundException, IOException, InterruptedException, NotBoundException {
    	testClient.subscribeWith().topicFilter(topics.getConnectedTopic()).qos(MqttQos.EXACTLY_ONCE).send();        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);
        
        runThread.start();
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
        
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});				
		
		// "Disconnect"
		registry.unbind("IElevator");
		UnicastRemoteObject.unexportObject(plc, true); // unexport IElevator mock object		
		UnicastRemoteObject.unexportObject(registry, true); // close registry
		
		waitUntilAssertsTrue(() -> output.toString().contains("Lost connection to RMI API") , 30, 1000);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testInitialRetainedMessages() throws InterruptedException, AlreadyBoundException, IOException, NotBoundException {
    	testClient.subscribeWith().topicFilter(topics.getNumElevatorsTopic()).qos(MqttQos.EXACTLY_ONCE).send();  
    	testClient.subscribeWith().topicFilter(topics.getNumFloorsTopic()).qos(MqttQos.EXACTLY_ONCE).send();    
    	testClient.subscribeWith().topicFilter(topics.getFloorHeightTopic()).qos(MqttQos.EXACTLY_ONCE).send();          
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);
    
        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		boolean[] receivedMessages = new boolean[3];
		receivedMessages[0] = receivedMessages[1] = receivedMessages[2] = false;
		
		for(int i = 0; i < 3; ++i) {					
			assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
				Mqtt3Publish received = incoming.receive();
				
		        assertTrue(received.getPayload().isPresent());

		        if(received.getTopic().compareTo(MqttUtf8String.of(topics.getNumElevatorsTopic())) == 0) {
		        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[0] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getNumFloorsTopic())) == 0) {
		        	assertEquals(3, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[1] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getFloorHeightTopic())) == 0) {
		        	assertEquals(4, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[2] = true;
		        }
			});					
		}
		
		assertTrue(receivedMessages[0] && receivedMessages[1] && receivedMessages[2]);
    }
    
    @Test
    void testInitialMessages() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getDirectionTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getAccelerationTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getCapacityTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getDoorsTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getFloorTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getPositionTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getSpeedTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getWeightTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getTargetTopic(0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(0, 0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(0, 1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(0, 2)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(0, 0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(0, 1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(0, 2)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getDirectionTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getAccelerationTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getCapacityTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getDoorsTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getFloorTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getPositionTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getSpeedTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getWeightTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getTargetTopic(1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(1, 0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(1, 1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(1, 2)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(1, 0)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(1, 1)).qos(MqttQos.EXACTLY_ONCE).send(); 
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(1, 2)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(2)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(2)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			final int elevatorMessageCount = 9 + 2 * 3;
			boolean[] receivedMessages = new boolean[2*elevatorMessageCount];
			boolean[] receivedFloorMessages = new boolean[6];
			for(int i = 0; i < 2*elevatorMessageCount; ++i) {
				receivedMessages[i] = false;
			}
			for(int i = 0; i < 6; ++i) {
				receivedFloorMessages[i] = false;
			}
			
			// initial elevator messages
			for(int i = 0; i < 2 * elevatorMessageCount + 6; ++i) {							
				Mqtt3Publish received = incoming.receive();						
		        assertTrue(received.getPayload().isPresent());

		        if(received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(0))) == 0) {
		        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[0] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(0))) == 0) {
		        	assertEquals(7, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[1] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getCapacityTopic(0))) == 0) {
		        	assertEquals(9, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[2] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(0))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[3] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getFloorTopic(0))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[4] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getPositionTopic(0))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[5] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getSpeedTopic(0))) == 0) {
		        	assertEquals(11, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[6] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getWeightTopic(0))) == 0) {
		        	receivedMessages[7] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getTargetTopic(0))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[8] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(0, 0))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[9] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(0, 1))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[10] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(0, 2))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[11] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(0, 0))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[12] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(0, 1))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[13] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(0, 2))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[14] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(1))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[15] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(1))) == 0) {
		        	assertEquals(8, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[16] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getCapacityTopic(1))) == 0) {
		        	assertEquals(10, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[17] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(1))) == 0) {
		        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[18] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getFloorTopic(1))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[19] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getPositionTopic(1))) == 0) {
		        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[20] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getSpeedTopic(1))) == 0) {
		        	assertEquals(12, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[21] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getWeightTopic(1))) == 0) {
		        	assertEquals(14, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[22] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getTargetTopic(1))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[23] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(1, 0))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[24] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(1, 1))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[25] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(1, 2))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[26] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(1, 0))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[27] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(1, 1))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[28] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(1, 2))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedMessages[29] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(0))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[0] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(1))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[1] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(2))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[2] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(0))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[3] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(1))) == 0) {
		        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[4] = true;
		        }
		        else if(received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(2))) == 0) {
		        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		        	receivedFloorMessages[5] = true;
		        }
			}
			
			boolean result = true;
			for(int i = 0; i < 2 * elevatorMessageCount; ++i) {
				result = result && receivedMessages[i];
			}				
			assertTrue(result);
			
			result = true;
			for(int i = 0; i < 6; ++i) {
				result = result && receivedFloorMessages[i];
			}				
			assertTrue(result);
		});
    }
    
    @Test
    void testDirectionChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getDirectionTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getCommittedDirection(0)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getCommittedDirection(0)).thenReturn(0);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testDirectionChangedElevator1() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getDirectionTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);
		
        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getCommittedDirection(1)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(1))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getCommittedDirection(1)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDirectionTopic(1))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testAccelerationChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getAccelerationTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorAccel(0)).thenReturn(8);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(0))));
        	assertEquals(8, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorAccel(0)).thenReturn(9);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(0))));
        	assertEquals(9, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testAccelerationChangedElevator1() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getAccelerationTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorAccel(1)).thenReturn(9);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(1))));
        	assertEquals(9, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorAccel(1)).thenReturn(10);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getAccelerationTopic(1))));
        	assertEquals(10, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testDoorStatusChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getDoorsTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorDoorStatus(0)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(0))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorDoorStatus(0)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testDoorStatusChangedElevator1() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getDoorsTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorDoorStatus(1)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(1))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorDoorStatus(1)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getDoorsTopic(1))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testFloorChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getFloorTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorFloor(0)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getFloorTopic(0))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorFloor(0)).thenReturn(0);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getFloorTopic(0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testPositionChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getPositionTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorPosition(0)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getPositionTopic(0))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorPosition(0)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getPositionTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testSpeedChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getSpeedTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorSpeed(0)).thenReturn(15);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getSpeedTopic(0))));
        	assertEquals(15, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorSpeed(0)).thenReturn(18);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getSpeedTopic(0))));
        	assertEquals(18, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testWeightChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getWeightTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorWeight(0)).thenReturn(20);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getWeightTopic(0))));
        	assertEquals(20, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorWeight(0)).thenReturn(5);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getWeightTopic(0))));
        	assertEquals(5, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testCapacityChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getCapacityTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorCapacity(0)).thenReturn(4);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getCapacityTopic(0))));
        	assertEquals(4, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorCapacity(0)).thenReturn(10);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getCapacityTopic(0))));
        	assertEquals(10, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testTargetChangedElevator0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getTargetTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getTarget(0)).thenReturn(1);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getTargetTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getTarget(0)).thenReturn(2);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getTargetTopic(0))));
        	assertEquals(2, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testButtonChangedElevator0Floor0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonTopic(0, 0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getElevatorButton(0, 0)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(0, 0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getElevatorButton(0, 0)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonTopic(0, 0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }

    @Test
    void testServiceChangedElevator0Floor0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getServicesFloorTopic(0, 0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getServicesFloors(0, 0)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(0, 0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getServicesFloors(0, 0)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getServicesFloorTopic(0, 0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonDownChangedFloor0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonDown(0)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonDown(0)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonDownChangedFloor1() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonDown(1)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(1))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonDown(1)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(1))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonDownChangedFloor2() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(2)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonDown(2)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(2))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonDown(2)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonDownTopic(2))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonUpChangedFloor0() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(0)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonUp(0)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(0))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonUp(0)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(0))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonUpChangedFloor1() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonUp(1)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(1))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonUp(1)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(1))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
    }
    
    @Test
    void testButtonUpChangedFloor2() throws AlreadyBoundException, IOException, InterruptedException {
    	testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(2)).qos(MqttQos.EXACTLY_ONCE).send();
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED);

        runThread.start();
		
        waitUntilAssertsTrue(() -> output.toString().contains("Using elevator simulator.") &&
        		output.toString().contains("Connected to RMI API.") );

		// initial elevator message
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {	
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
		});
		
		when(plc.getFloorButtonUp(2)).thenReturn(true);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(2))));
        	assertEquals(1, received.getPayload().get().asIntBuffer().get());
		});
		
		when(plc.getFloorButtonUp(2)).thenReturn(false);
		
		assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
			Mqtt3Publish received = incoming.receive();						
	        assertTrue(received.getPayload().isPresent());
	        assertEquals(0, received.getTopic().compareTo(MqttUtf8String.of(topics.getButtonUpTopic(2))));
        	assertEquals(0, received.getPayload().get().asIntBuffer().get());
		});
    }
    
}
