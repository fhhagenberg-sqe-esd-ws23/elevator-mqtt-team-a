package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public class ElevatorsMqttAdapter implements PropertyChangeListener {

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
			elevators[elevator].addPropertyChangeListener(this);
			elevatorUpdaters[elevator] = new ElevatorUpdater(elevators[elevator]);
		}
		
		for(int floor = 0; floor < floors.length; ++floor) {
			floors[floor] = new Floor(plc, floor);
			floors[floor].addPropertyChangeListener(this);
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO: React to property changes in elevators and floors and send MQTT messages
	}
	
}
