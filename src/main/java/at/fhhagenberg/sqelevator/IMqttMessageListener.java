package at.fhhagenberg.sqelevator;

public interface IMqttMessageListener {
	void setCommittedDirection(int elevator, int direction);
	void setServicesFloor(int elevator, int floor, boolean service);
	void setTarget(int elevator, int target);
}
