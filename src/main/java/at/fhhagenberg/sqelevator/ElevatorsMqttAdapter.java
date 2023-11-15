package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.Arrays;

public class ElevatorsMqttAdapter {

	private Elevator[] elevators;
	private Floor[] floors;
	private IUpdater[] updaters;
	
	public ElevatorsMqttAdapter(IElevator plc) throws RemoteException {
		int numElevators = plc.getElevatorNum();
		int numFloors = plc.getFloorNum();
		elevators = new Elevator[numElevators];
		floors = new Floor[numFloors];
		updaters = new IUpdater[elevators.length + floors.length];
		
		for(int i = 0; i < elevators.length; ++i) {
			Elevator elevator = new Elevator(plc, i);
			elevators[i] = elevator;
			updaters[i] = new ElevatorUpdater(elevator);
		}
		
		for(int i = 0; i < floors.length; ++i) {
			Floor floor = new Floor(plc, i);
			floors[i] = floor;
			updaters[numElevators + i] = new FloorUpdater(floor);
		}
	}
	
	public void run() throws RemoteException {
		
		while(true) {
			for(IUpdater updater : updaters) {
				updater.update();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Elevator[] getElevators() {
		return Arrays.copyOf(elevators, elevators.length);
	}

	public Floor[] getFloors() {
		return Arrays.copyOf(floors, floors.length);
	}

	public IUpdater[] getUpdaters() {
		return Arrays.copyOf(updaters, updaters.length);
	}
	
}
