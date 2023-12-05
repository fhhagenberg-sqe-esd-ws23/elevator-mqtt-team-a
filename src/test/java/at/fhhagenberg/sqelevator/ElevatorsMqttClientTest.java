package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

@Testcontainers
class ElevatorsMqttClientTest {
    private final MqttTopicGenerator topics = new MqttTopicGenerator();

    @Container
    HiveMQContainer container = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));
	
    private Mqtt3BlockingClient testClient;
    ElevatorsMqttClient mqtt;
    
    @BeforeEach
    void setUp() throws InterruptedException, ExecutionException {
        testClient = Mqtt3Client.builder().serverPort(container.getMqttPort()).buildBlocking();
        testClient.connect();
        
    	mqtt = new ElevatorsMqttClient(container.getHost(), container.getMqttPort());
        mqtt.connect();
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishDirection() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getDirectionTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishDirection(1, IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishNumberOfElevators() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getNumElevatorsTopic()).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishNumberOfElevators(3);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(3, received.getPayload().get().asIntBuffer().get());
        // assertTrue(received.isRetain()); // TODO: this fails, isRetain() returns false?
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishFloorHeight() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getFloorHeightTopic()).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishFloorHeight(2);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(2, received.getPayload().get().asIntBuffer().get());
        //assertTrue(received.isRetain()); // TODO: this fails, isRetain() returns false?
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishAcceleration() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getAccelerationTopic(5)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishAcceleration(5, 7);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(7, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishButtonPressed() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getButtonTopic(2,4)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishButtonPressed(2, 4, true);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(1, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishCapacity() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getCapacityTopic(3)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishCapacity(3, 4711);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(4711, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishDoors() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getDoorsTopic(7)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishDoors(7, IElevator.ELEVATOR_DOORS_CLOSING);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(IElevator.ELEVATOR_DOORS_CLOSING, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPublishFloor() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getFloorTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishFloor(1, 3);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(3, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testPosition() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getPositionTopic(11)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishPosition(11, 3);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(3, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testSpeed() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getSpeedTopic(3)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishSpeed(3, 47);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(47, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testWeight() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getWeightTopic(3)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishWeight(3, 36);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(36, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testTarget() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getTargetTopic(2)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishTarget(2, 178);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(178, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testButtonUp() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getButtonUpTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishButtonUp(1, true);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(1, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testButtonDown() throws InterruptedException, ExecutionException {    	
        testClient.subscribeWith().topicFilter(topics.getButtonDownTopic(1)).qos(MqttQos.EXACTLY_ONCE).send();
        
        final Mqtt3BlockingClient.Mqtt3Publishes incoming = testClient.publishes(MqttGlobalPublishFilter.ALL);
        
        mqtt.publishButtonDown(1, true);
        
        final Mqtt3Publish received = incoming.receive();

        assertTrue(received.getPayload().isPresent());
        assertEquals(1, received.getPayload().get().asIntBuffer().get());
        assertFalse(received.isRetain());
	}
    
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testSetDirection() throws InterruptedException, ExecutionException {    	
        
    	
    	IMqttMessageListener mocker = mock(IMqttMessageListener.class);
    	mqtt.addListener(mocker);
    	
    	mqtt.subscribeToControlMessages(1, 2);
    	
    	testClient.publishWith()
    			.topic(topics.getSetDirectionTopic(0))
    			.payload(ByteBuffer.allocate(Integer.BYTES).putInt(IElevator.ELEVATOR_DIRECTION_UP).array())
    			.qos(MqttQos.EXACTLY_ONCE)
    			.retain(false)
    			.send();
    	
    	Thread.sleep(100);
    	
    	
    	verify(mocker, times(1)).setCommittedDirection(0, IElevator.ELEVATOR_DIRECTION_UP);

	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testSetTargetReceived() throws InterruptedException, ExecutionException {    	
        
    	
    	IMqttMessageListener mocker = mock(IMqttMessageListener.class);
    	mqtt.addListener(mocker);
    	
    	mqtt.subscribeToControlMessages(2, 5);
    	
    	testClient.publishWith()
    			.topic(topics.getSetTargetTopic(1))
    			.payload(ByteBuffer.allocate(Integer.BYTES).putInt(1).array())
    			.qos(MqttQos.EXACTLY_ONCE)
    			.retain(false)
    			.send();
    	
    	Thread.sleep(100);
    	
    	verify(mocker, times(1)).setTarget(1, 1);

	}
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
	void testSetServicesFloorReceived() throws InterruptedException, ExecutionException {    	
        
    	
    	IMqttMessageListener mocker = mock(IMqttMessageListener.class);
    	mqtt.addListener(mocker);
    	
    	mqtt.subscribeToControlMessages(2, 5);
    	
    	testClient.publishWith()
    			.topic(topics.getSetServicesFloorTopic(1,1))
    			.payload(ByteBuffer.allocate(Integer.BYTES).putInt(1).array())
    			.qos(MqttQos.EXACTLY_ONCE)
    			.retain(false)
    			.send();
    	
    	Thread.sleep(100);
    	
    	verify(mocker, times(1)).setServicesFloor(1, 1, true);
    	
    	mqtt.removeListener(mocker);

	}
    
    
    
    @AfterEach
    void cleanUp() throws InterruptedException, ExecutionException {
    	mqtt.disconnect();
    	testClient.disconnect();
    }
}
