package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MqttTopicGeneratorTest {
	private MqttTopicGenerator generator;

	@BeforeEach
	void setup() {
		generator = new MqttTopicGenerator();
	}
	
	@Test
	void testGetTopic_Empty() {
		assertEquals("", generator.getTopic());
	}
	
	@Test
	void testGetTopic_OneParameter() {
		assertEquals("topic", generator.getTopic("topic"));
	}

	@Test
	void testGetElevatorTopic() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR;
		assertEquals(expected, generator.getElevatorTopic());
	}
	
	@Test
	void testGetFloorTopic() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR;
		assertEquals(expected, generator.getFloorTopic());
	}
	
	@Test
	void testGetNumElevatorsTopic() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_COUNT;
		assertEquals(expected, generator.getNumElevatorsTopic());
	}
	
	@Test
	void testGetNumFloorsTopic() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_COUNT;
		assertEquals(expected, generator.getNumFloorsTopic());
	}
	
	@Test
	void testGetFloorHeightTopic() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_HEIGHT;
		assertEquals(expected, generator.getFloorHeightTopic());
	}
	
	@Test
	void testGetDirectionTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_DIRECTION;
		assertEquals(expected, generator.getDirectionTopic(1));
	}
	
	@Test
	void testGetAccelerationTopic_Elevator2() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "2"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ACCELERATION;
		assertEquals(expected, generator.getAccelerationTopic(2));
	}
	
	@Test
	void testGetButtonTopic_Elevator1Floor2() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_BUTTON
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "2";
		assertEquals(expected, generator.getButtonTopic(1, 2));
	}
	
	@Test
	void testGetCapacityTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_CAPACITY;
		assertEquals(expected, generator.getCapacityTopic(1));
	}
	
	@Test
	void testGetDoorsTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_DOORS;
		assertEquals(expected, generator.getDoorsTopic(1));
	}
	
	@Test
	void testGetFloorTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR;
		assertEquals(expected, generator.getFloorTopic(1));
	}
	
	@Test
	void testGetPositionTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_POSITION;
		assertEquals(expected, generator.getPositionTopic(1));
	}
	
	@Test
	void testGetSpeedTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SPEED;
		assertEquals(expected, generator.getSpeedTopic(1));
	}
	
	@Test
	void testGetWeightTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_WEIGHT;
		assertEquals(expected, generator.getWeightTopic(1));
	}
	
	@Test
	void testGetServicesTopic_Elevator1Floor2() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SERVICES_FLOOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "2";
		assertEquals(expected, generator.getServicesFloorTopic(1, 2));
	}
	
	@Test
	void testGetTargetTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_TARGET;
		assertEquals(expected, generator.getTargetTopic(1));
	}
	
	@Test
	void testGetSetDircetionTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SET_DIRECTION;
		assertEquals(expected, generator.getSetDirectionTopic(1));
	}
	
	@Test
	void testGetSetTargetTopic_Elevator1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SET_TARGET;
		assertEquals(expected, generator.getSetTargetTopic(1));
	}
	
	@Test
	void testGetSetServicesTopic_Elevator1Floor2() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_ELEVATOR 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SET_SERVICES_FLOOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "2";
		assertEquals(expected, generator.getSetServicesFloorTopic(1, 2));
	}
	
	@Test
	void testGetButtonUpTopic_Floor1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_UP;
		assertEquals(expected, generator.getButtonUpTopic(1));
	}
	
	@Test
	void testGetButtonDownTopic_Floor1() {
		final String expected = MqttTopicGenerator.TOPIC_LEVEL_BUILDING 
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_FLOOR
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ "1"
				+ MqttTopicGenerator.TOPIC_LEVEL_SEPERATOR
				+ MqttTopicGenerator.TOPIC_LEVEL_DOWN;
		assertEquals(expected, generator.getButtonDownTopic(1));
	}
}
