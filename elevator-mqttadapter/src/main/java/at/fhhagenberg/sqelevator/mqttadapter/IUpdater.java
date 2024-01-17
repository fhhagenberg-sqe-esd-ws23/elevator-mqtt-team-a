package at.fhhagenberg.sqelevator.mqttadapter;

import java.rmi.RemoteException;

/**
 * Common interface for updater classes (e.g. ElevatorUpdater, FloorUpdater) used by the main ElevatorsMqttAdapter class.
 */
public interface IUpdater {

	/**
	 * Update the associated object from the RMI API.
	 * @throws RemoteException if the connection to the RMI API is lost
	 */
	public void update() throws RemoteException;

}
