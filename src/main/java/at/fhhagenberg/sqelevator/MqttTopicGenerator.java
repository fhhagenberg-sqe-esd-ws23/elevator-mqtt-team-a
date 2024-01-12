package at.fhhagenberg.sqelevator;

/**
 * Utility class which builds and provides MQTT topics for the MQTT client.
 */
public class MqttTopicGenerator {

	public final static char TOPIC_LEVEL_SEPERATOR = '/';
	
	public final static String TOPIC_LEVEL_BUILDING = "building";
	public final static String TOPIC_LEVEL_ELEVATOR = "elevator";
	public final static String TOPIC_LEVEL_FLOOR = "floor";
	public final static String TOPIC_LEVEL_COUNT = "count";
	public final static String TOPIC_LEVEL_HEIGHT = "height";
	public final static String TOPIC_LEVEL_UP = "up";
	public final static String TOPIC_LEVEL_DOWN = "down";
	public final static String TOPIC_LEVEL_DIRECTION = "direction";
	public final static String TOPIC_LEVEL_ACCELERATION  = "acceleration";
	public final static String TOPIC_LEVEL_BUTTON = "button";
	public final static String TOPIC_LEVEL_CAPACITY = "capacity";
	public final static String TOPIC_LEVEL_DOORS = "doors";
	public final static String TOPIC_LEVEL_POSITION = "position";
	public final static String TOPIC_LEVEL_SPEED = "speed";
	public final static String TOPIC_LEVEL_WEIGHT = "weight";
	public final static String TOPIC_LEVEL_SERVICES_FLOOR = "servicesfloor";
	public final static String TOPIC_LEVEL_TARGET = "target";
	public final static String TOPIC_LEVEL_SET_DIRECTION = "setdirection";
	public final static String TOPIC_LEVEL_SET_SERVICES_FLOOR = "setservicesFloor";
	public final static String TOPIC_LEVEL_SET_TARGET = "setTarget";
	public final static String TOPIC_LEVEL_CONNECTED = "connected";
		
	public String getTopic(String... topicLevels) {
		if(topicLevels.length == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(topicLevels[0]);
		
		for(int i = 1; i < topicLevels.length; ++i) {
			sb.append(TOPIC_LEVEL_SEPERATOR);
			sb.append(topicLevels[i]);
		}
		
		return sb.toString();
	}
	
	public String getElevatorTopic() {
		return getTopic(TOPIC_LEVEL_BUILDING, TOPIC_LEVEL_ELEVATOR);
	}
	
	public String getFloorTopic() {
		return getTopic(TOPIC_LEVEL_BUILDING, TOPIC_LEVEL_FLOOR);		
	}
	
	public String getConnectedTopic() {
		return getTopic(TOPIC_LEVEL_BUILDING, TOPIC_LEVEL_CONNECTED);
	}
	
	public String getNumElevatorsTopic() {
		return getTopic(getElevatorTopic(), TOPIC_LEVEL_COUNT);		
	}
	
	public String getNumFloorsTopic() {
		return getTopic(getFloorTopic(), TOPIC_LEVEL_COUNT);	
	}
	
	public String getFloorHeightTopic() {
		return getTopic(getFloorTopic(), TOPIC_LEVEL_HEIGHT);	
	}
	
	public String getDirectionTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_DIRECTION);
	}
	
	public String getAccelerationTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_ACCELERATION);
	}
	
	public String getButtonTopic(int elevator, int floor) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_BUTTON, String.valueOf(floor));
	}
	
	public String getCapacityTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_CAPACITY);
	}
	
	public String getDoorsTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_DOORS);
	}
	
	public String getFloorTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_FLOOR);
	}

	public String getPositionTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_POSITION);
	}
	
	public String getSpeedTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_SPEED);
	}
	
	public String getWeightTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_WEIGHT);
	}
	
	public String getServicesFloorTopic(int elevator, int floor) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_SERVICES_FLOOR, String.valueOf(floor));
	}
	
	public String getTargetTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_TARGET);
	}
	
	public String getSetDirectionTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_SET_DIRECTION);
	}
	
	public String getSetTargetTopic(int elevator) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_SET_TARGET);
	}
	
	public String getSetServicesFloorTopic(int elevator, int floor) {
		return getTopic(getElevatorTopic(), String.valueOf(elevator), TOPIC_LEVEL_SET_SERVICES_FLOOR, String.valueOf(floor));
	}
	
	public String getButtonUpTopic(int floor) {
		return getTopic(getFloorTopic(), String.valueOf(floor), TOPIC_LEVEL_UP);
	}
	
	public String getButtonDownTopic(int floor) {
		return getTopic(getFloorTopic(), String.valueOf(floor), TOPIC_LEVEL_DOWN);
	}
}
