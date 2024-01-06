package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;

import sqelevator.IElevator;

/**
 * Class for updating Floor objects from the IElevator API.
 */
public class FloorUpdater implements IUpdater {
	
	private final Floor floor;
	private final IElevator plc;
	private final int number;

	/**
	 * Create a new Updater object for the given floor.
	 * @param floor the floor to create the updater for
	 */
	public FloorUpdater(Floor floor) {		
		if(floor == null) {
			throw new IllegalArgumentException("Floor must be valid!"); 
		}
		
		this.floor = floor;
		this.plc = floor.getPlc();
		this.number = floor.getNumber();		
	}

	public void update() throws RemoteException {		
		floor.setButtonDown(plc.getFloorButtonDown(number));
		floor.setButtonUp(plc.getFloorButtonUp(number));
	}
}
