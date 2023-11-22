package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.Arrays;

public class Building {

	private final Elevator[] elevators;
	private final Floor[] floors;
	
	public Building(IElevator plc) throws RemoteException {		
		if(plc == null) {
			throw new IllegalArgumentException("Plc must be valid!"); 
		}
		
		int numElevators = plc.getElevatorNum();
		int numFloors = plc.getFloorNum();
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
		
	public Elevator[] getElevators() {
		return Arrays.copyOf(elevators, elevators.length);
	}

	public Floor[] getFloors() {
		return Arrays.copyOf(floors, floors.length);
	}
	
}
