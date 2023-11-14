package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.Arrays;

public class ElevatorsMqttAdapter {

	private Elevator[] elevators;
	private Floor[] floors;
	private ElevatorUpdater[] elevatorUpdaters;
	private FloorUpdater[] floorUpdaters;
	
	public ElevatorsMqttAdapter(IElevator plc) throws RemoteException {
		int numElevators = plc.getElevatorNum();
		int numFloors = plc.getFloorNum();
		elevators = new Elevator[numElevators];
		elevatorUpdaters = new ElevatorUpdater[elevators.length];
		floors = new Floor[numFloors];
		floorUpdaters = new FloorUpdater[floors.length];
		
		for(int elevator = 0; elevator < elevators.length; ++elevator) {
			elevators[elevator] = new Elevator(plc, elevator);
			elevatorUpdaters[elevator] = new ElevatorUpdater(elevators[elevator]);
		}
		
		for(int floor = 0; floor < floors.length; ++floor) {
			floors[floor] = new Floor(plc, floor);
			floorUpdaters[floor] = new FloorUpdater(floors[floor]);
		}
		
		// TODO: Send initial MQTT messages
	}
	
	public void run() throws RemoteException {
		
		while(true) {
			for(int elevator = 0; elevator < elevators.length; ++elevator) {
				elevatorUpdaters[elevator].update();
			}
			
			for(int floor = 0; floor < floors.length; ++floor) {
				floorUpdaters[floor].update();
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

	public ElevatorUpdater[] getElevatorUpdaters() {
		return Arrays.copyOf(elevatorUpdaters, elevatorUpdaters.length);
	}

	public FloorUpdater[] getFloorUpdaters() {
		return Arrays.copyOf(floorUpdaters, floorUpdaters.length);
	}
	
}
