package at.fhhagenberg.sqelevator;

/**
 * Interface for MQTT control message listeners which can be added to the the ElevatorsMQTTClient.
 */
public interface IMqttMessageListener {

	/**
	 * Sets the committed direction of the specified elevator (up / down / uncommitted).
	 * @param elevatorNumber elevator number whose committed direction is being set
	 * @param direction direction being set where up=0, down=1 and uncommitted=2
	 */
	void setCommittedDirection(int elevator, int direction);

	/**
	 * Sets whether or not the specified elevator will service the specified floor (yes/no).
	 * @param elevatorNumber elevator number whose service is being defined
	 * @param floor floor whose service by the specified elevator is being set
	 * @param service indicates whether the floor is serviced by the specified elevator (yes=true,no=false)
	 */
	void setServicesFloor(int elevator, int floor, boolean service);

	/**
	 * Sets the floor target of the specified elevator.
	 * @param elevator elevator number whose target floor is being set
	 * @param target floor number which the specified elevator is to target
	 */
	void setTarget(int elevator, int target);
}
