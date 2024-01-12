package at.fhhagenberg.sqelevator;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAckReturnCode;

/**
 * This class provides the MQTT client for the Elevators MQTT Adapter.
 * It wraps a HiveMQ MQTT client and provides methods to easily publish various messages and subscribe to control messages.
 * IMqttMessageListener objects can be added to get notified of control messages.
 */
public class ElevatorsMqttClient {
	
	private final Mqtt3AsyncClient client;
	private final HashSet<IMqttMessageListener> listeners = new HashSet<IMqttMessageListener>();
	private final MqttTopicGenerator topics = new MqttTopicGenerator();
	private boolean connected = false;

	/**
	 * Create and build new MQTT client.
	 * @param host address to the MQTT broker server
	 * @param port port of the MQTT broker service on the server
	 */
	public ElevatorsMqttClient(String host, int port) {
		client = MqttClient.builder()
		        .useMqttVersion3()
		        .identifier(UUID.randomUUID().toString())
		        .serverHost(host)
		        .serverPort(port)
		        .buildAsync();
	}

	/**
	 * Provides the connection status of the MQTT client.
	 * @return if the MQTT client is connected to a broker (true) or not (false)
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Adds a MQTT control message listener.
	 * @param listener MQTT control message listener to add
	 */
	public void addListener(IMqttMessageListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a MQTT control message listener.
	 * @param listener MQTT control message listener to remove
	 */
	public void removeListener(IMqttMessageListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Connects the MQTT client to the MQTT broker.
	 * @return if the connection attempt was successful (true) or not (false)
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean connect() throws InterruptedException, ExecutionException {
		if(isConnected()) {
			return true;
		}
		
		Mqtt3ConnAck result = client.connect().get();
		Mqtt3ConnAckReturnCode code = result.getReturnCode();
		connected = code == Mqtt3ConnAckReturnCode.SUCCESS;		
		return connected;	
	}

	/**
	 * Subscribe to all control messages required for the system.
	 * @param numberOfElevators the number of elevators in the system
	 * @param numberOfFloors the number of floors in the system
	 * @return if the subscription to all control messages was successful (true) or not (false)
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean subscribeToControlMessages(int numberOfElevators, int numberOfFloors) throws InterruptedException, ExecutionException {
		if(!isConnected()) {
			return false;
		}
				
		for(int i = 0; i < numberOfElevators; ++i) {
			final int elevator = i;
			
			List<Mqtt3SubAckReturnCode> codes = client.subscribeWith()
	        .topicFilter(topics.getSetDirectionTopic(elevator))
	        .callback(publish -> {
	        	if(publish.getPayload().isPresent()) {
		        	setDirectionReceived(elevator, publish.getPayload().get().getInt());	        		
	        	}
	        })
	        .send().get().getReturnCodes();
			
			for(Mqtt3SubAckReturnCode code : codes) {
				if(code == Mqtt3SubAckReturnCode.FAILURE) {
					unsubscribeAll();
					return false;
				}
			}
			
			codes = client.subscribeWith()
	        .topicFilter(topics.getSetTargetTopic(elevator))
	        .callback(publish -> {
	        	if(publish.getPayload().isPresent()) {
		        	setTargetReceived(elevator, publish.getPayload().get().getInt());	        		
	        	}
	        })
	        .send().get().getReturnCodes();			

			for(Mqtt3SubAckReturnCode code : codes) {
				if(code == Mqtt3SubAckReturnCode.FAILURE) {
					unsubscribeAll();
					return false;
				}
			}
			
			for(int j = 0; j < numberOfFloors; ++j) {
				final int floor = j;
				
				codes = client.subscribeWith()
		        .topicFilter(topics.getSetServicesFloorTopic(elevator, floor))
		        .callback(publish -> {
		        	if(publish.getPayload().isPresent()) {
			        	setServicesFloorReceived(elevator, floor, publish.getPayload().get().getInt() == 1);   		
		        	}
		        })
		        .send().get().getReturnCodes();
				
				for(Mqtt3SubAckReturnCode code : codes) {
					if(code == Mqtt3SubAckReturnCode.FAILURE) {
						unsubscribeAll();
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Unsubscribe from all control messages.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void unsubscribeAll() throws InterruptedException, ExecutionException {
		client.unsubscribeWith()
		.topicFilter(MqttTopicGenerator.TOPIC_LEVEL_BUILDING)
        .send().get();
	}
	
	private void setDirectionReceived(int elevator, int direction) {
		for(IMqttMessageListener listener : listeners) {
			listener.setCommittedDirection(elevator, direction);
		}
	}

	private void setTargetReceived(int elevator, int target) {
		for(IMqttMessageListener listener : listeners) {
			listener.setTarget(elevator, target);
		}
	}
	
	private void setServicesFloorReceived(int elevator, int floor, boolean service) {
		for(IMqttMessageListener listener : listeners) {
			listener.setServicesFloor(elevator, floor, service);
		}
	}

	/**
	 * Publish a MQTT message.
	 * @param topic the topic to publish the message to
	 * @param payload the payload to publish the message with
	 * @param retain whether the message should be a retained message (true) or not (false)
	 */
	public void publish(String topic, ByteBuffer payload, boolean retain) {
		try {
			client.publishWith()
			.topic(topic)
			.payload(payload.array())
			.qos(MqttQos.EXACTLY_ONCE)
			.retain(retain)
			.send()
	        .whenComplete((mqtt3Publish, throwable) -> {
	            if (throwable != null) {
	                // TODO: Handle failure to publish
	            } else {
	                // TODO: Handle successful publish, e.g. logging or incrementing a metric kek
	            }
	        })
			.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Publish a retained MQTT message.
	 * @param topic the topic to publish the message to
	 * @param payload the payload to publish the message with
	 */
	public void publishRetained(String topic, ByteBuffer payload) {
		publish(topic, payload, true);
	}

	/**
	 * Publish a MQTT message which is not a retained message.
	 * @param topic the topic to publish the message to
	 * @param payload the payload to publish the message with
	 */
	public void publishNotRetained(String topic, ByteBuffer payload) {
		publish(topic, payload, false);
	}
	
	public void publishNumberOfElevators(int numberOfElevators) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(numberOfElevators);		
		publishRetained(topics.getNumElevatorsTopic(), payload);
	}
	
	public void publishNumberOfFloors(int numberOfFloors) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(numberOfFloors);
		publishRetained(topics.getNumFloorsTopic(), payload);
	}
	
	public void publishFloorHeight(int floorHeight) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(floorHeight);
		publishRetained(topics.getFloorHeightTopic(), payload);
	}
	
	public void publishConnected(boolean connected) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(connected ? 1 : 0);
		publishRetained(topics.getConnectedTopic(), payload);
	}

	public void publishDirection(int elevator, int direction) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(direction);
		publishNotRetained(topics.getDirectionTopic(elevator), payload);
	}
	
	public void publishAcceleration(int elevator, int acceleration) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(acceleration);
		publishNotRetained(topics.getAccelerationTopic(elevator), payload);
	}
	
	public void publishButtonPressed(int elevator, int floor, boolean button) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(button ? 1 : 0);
		publishNotRetained(topics.getButtonTopic(elevator, floor), payload);
	}
	
	public void publishCapacity(int elevator, int capacity) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(capacity);
		publishNotRetained(topics.getCapacityTopic(elevator), payload);
	}
	
	public void publishDoors(int elevator, int doors) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(doors);
		publishNotRetained(topics.getDoorsTopic(elevator), payload);
	}
	
	public void publishFloor(int elevator, int floor) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(floor);
		publishNotRetained(topics.getFloorTopic(elevator), payload);
	}
	
	public void publishPosition(int elevator, int position) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(position);
		publishNotRetained(topics.getPositionTopic(elevator), payload);
	}
	
	public void publishSpeed(int elevator, int speed) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(speed);
		publishNotRetained(topics.getSpeedTopic(elevator), payload);
	}
	
	public void publishWeight(int elevator, int weight) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(weight);
		publishNotRetained(topics.getWeightTopic(elevator), payload);
	}
	
	public void publishServicesFloor(int elevator, int floor, boolean service) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(service ? 1 : 0);
		publishNotRetained(topics.getServicesFloorTopic(elevator, floor), payload);
	}
		
	public void publishTarget(int elevator, int target) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(target);
		publishNotRetained(topics.getTargetTopic(elevator), payload);
	}
	
	public void publishButtonUp(int floor, boolean pressed) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(pressed ? 1 : 0);
		publishNotRetained(topics.getButtonUpTopic(floor), payload);
	}
		
	public void publishButtonDown(int floor, boolean pressed) {
		ByteBuffer payload = ByteBuffer.allocate(Integer.BYTES).putInt(pressed ? 1 : 0);
		publishNotRetained(topics.getButtonDownTopic(floor), payload);
	}

	/**
	 * Unsubscribe from all control messages and disconnect the client from the broker.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void disconnect() throws InterruptedException, ExecutionException {
		unsubscribeAll();
		client.disconnect().get();
		connected = false;
	}

}
