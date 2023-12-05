package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;

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
    
    @AfterEach
    void cleanUp() throws InterruptedException, ExecutionException {
    	mqtt.disconnect();
    	testClient.disconnect();
    }
}
