package at.fhhagenberg.sqelevator;

/**
 * Common interface for bridges between classes (e.g. Elevator, Floor) and a MQTT client used by the main ElevatorsMqttAdapter class.
 */
public interface IMqttBridge {

	/**
	 * Start the bridge.
	 */
	public void start();

	/**
	 * Stop the bridge.
	 */
	public void stop();

}
