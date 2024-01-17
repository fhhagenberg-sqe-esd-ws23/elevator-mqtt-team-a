package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.Arrays;

import sqelevator.IElevator;

/**
 * Class representing a building with elevators and floors.
 * The building is initialized in the constructor using information from the control unit.
 */
public class Building {

	private final Elevator[] elevators;
	private final Floor[] floors;
	private final int floorHeight;

	/**
	 * Create a new building from the given IElevator API.
	 * @param plc the IElevator API to create the building from
	 * @throws RemoteException if the connection to the IElevator API is broken
	 */
	public Building(IElevator plc) throws RemoteException {		
		if(plc == null) {
			throw new IllegalArgumentException("Plc must be valid!"); 
		}
		
		int numElevators = plc.getElevatorNum();
		int numFloors = plc.getFloorNum();
		floorHeight = plc.getFloorHeight();
		elevators = new Elevator[numElevators];
		floors = new Floor[numFloors];
		
		for(int i = 0; i < elevators.length; ++i) {
			Elevator elevator = new Elevator(plc, i);
			elevators[i] = elevator;
		}
		
		for(int i = 0; i < floors.length; ++i) {
			Floor floor = new Floor(plc, i);
			floors[i] = floor;
		}
	}

	/**
	 * Provides a copy of the array with the elevators in the building.
	 * @return an array with the elevators in the building
	 */
	public Elevator[] getElevators() {
		return Arrays.copyOf(elevators, elevators.length);
	}

	/**
	 * Provides a copy of the array with the floors in the building.
	 * @return an array with the floors in the building
	 */
	public Floor[] getFloors() {
		return Arrays.copyOf(floors, floors.length);
	}

	/**
	 * Provides the number of elevators in the building.
	 * @return the number of elevators in the building
	 */
	public int getElevatorCount() {
		return elevators.length;
	}

	/**
	 * Provides the number of floors in the building.
	 * @return the number of floors in the building
	 */
	public int getFloorCount() {
		return floors.length;
	}
	
	/**
	 * Provides the height of the floors in the building.
	 * @return the height of the floors in the building
	 */
	public int floorHeight() {
		return floorHeight;
	}
}
