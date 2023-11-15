package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;

public class FloorUpdater implements IUpdater {
	
	private Floor floor;
	private IElevator plc;
	private int number;

	public FloorUpdater(Floor floor) {
		this.floor = floor;
		this.plc = floor.getPlc();
		this.number = floor.getNumber();
		
	}
	
	public void update() throws RemoteException {		
		floor.setButtonDown(plc.getFloorButtonDown(number));
		floor.setButtonUp(plc.getFloorButtonUp(number));
		
	}
}
