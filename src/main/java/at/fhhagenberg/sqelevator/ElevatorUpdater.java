package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;

import sqelevator.IElevator;

/**
 * Class for updating Elevator objects from the IElevator API.
 */
public class ElevatorUpdater implements IUpdater {

	private final Elevator elevator;
	private final IElevator plc;
	private final int number;
	private final int numberOfFloors;

	/**
	 * Create a new Updater object for the given elevator.
	 * @param elevator the elevator to create the updater for
	 */
	public ElevatorUpdater(Elevator elevator) {
		this.elevator = elevator;
		plc = elevator.getPlc();
		number = elevator.getNumber();
		numberOfFloors = elevator.getNumberOfFloors();
	}

	public void update() throws RemoteException {
		elevator.setCommittedDirection(plc.getCommittedDirection(number));
		elevator.setAcceleration(plc.getElevatorAccel(number));

		for(int floor = 0; floor < numberOfFloors; ++floor) {
			elevator.setStopRequest(floor, plc.getElevatorButton(number, floor));
		}

		elevator.setCapacity(plc.getElevatorCapacity(number));
		elevator.setDoorStatus(plc.getElevatorDoorStatus(number));
		elevator.setFloor(plc.getElevatorFloor(number));
		elevator.setPosition(plc.getElevatorPosition(number));
		elevator.setSpeed(plc.getElevatorSpeed(number));
		elevator.setWeight(plc.getElevatorWeight(number));

		for(int floor = 0; floor < numberOfFloors; ++floor) {
			elevator.setServicesFloor(floor, plc.getServicesFloors(number, floor));
		}

		elevator.setTarget(plc.getTarget(number));
	}

}
