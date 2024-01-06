package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.Arrays;

import sqelevator.IElevator;

public class Elevator {
	
	private final IElevator plc;
	private final int number;
	private final int numberOfFloors;
	private int committedDirection;
	private int acceleration;
	private boolean[] stopRequests;
	private int capacity;
	private int doorStatus;
	private int floor;
	private int position;
	private int speed;
	private int weight;
	private boolean[] servicedFloors;
	private int target;
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public final static String COMMITTED_DIRECTION_PROPERTY_NAME = "CommittedDirection";	
	
	public final static String ACCELERATION_PROPERTY_NAME = "Acceleration";	

	public final static String STOP_REQUESTS_PROPERTY_NAME = "StopRequests";

	public final static String CAPACITY_PROPERTY_NAME = "capacity";

	public final static String DOOR_STATUS_PROPERTY_NAME = "doorStatus";

	public final static String FLOOR_PROPERTY_NAME = "floor";

	public final static String POSITION_PROPERTY_NAME = "position";

	public final static String SPEED_PROPERTY_NAME = "speed";

	public final static String WEIGHT_PROPERTY_NAME = "weight";

	public final static String SERVICED_FLOORS_PROPERTY_NAME = "servicedFloors";

	public final static String TARGET_PROPERTY_NAME = "target";
	
	public Elevator(IElevator plc, int number) throws RemoteException
	{
		if(plc == null) {
			throw new IllegalArgumentException("Plc must be valid!"); 
		}
		if (number < 0) {
			throw new IllegalArgumentException("The number of the Elevator must be >=0!");
		}
		
		this.plc = plc;
		this.number = number;
		numberOfFloors = plc.getFloorNum();
		
		this.setCommittedDirection(plc.getCommittedDirection(number));
		this.setAcceleration(plc.getElevatorAccel(number));
		
		stopRequests = new boolean[numberOfFloors];
		
		for(int floor = 0; floor < numberOfFloors; ++floor) {
			this.setStopRequest(floor, plc.getElevatorButton(number, floor));
		}
		
		this.setCapacity(plc.getElevatorCapacity(number));
		this.setDoorStatus(plc.getElevatorDoorStatus(number));
		this.setFloor(plc.getElevatorFloor(number));
		this.setPosition(plc.getElevatorPosition(number));
		this.setSpeed(plc.getElevatorSpeed(number));
		this.setWeight(plc.getElevatorWeight(number));

		servicedFloors = new boolean[numberOfFloors];

		for(int floor = 0; floor < numberOfFloors; ++floor) {
			this.setServicesFloor(floor, plc.getServicesFloors(number, floor));
		}
		
		this.setTarget(plc.getTarget(number));
	}

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

	public IElevator getPlc() {
		return plc;
	}

	public int getNumber() {
		return number;
	}

	public int getCommittedDirection() {
		return committedDirection;
	}
	
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	public void setCommittedDirection(int committedDirection) throws RemoteException {
		if (committedDirection != IElevator.ELEVATOR_DIRECTION_DOWN
				&& committedDirection != IElevator.ELEVATOR_DIRECTION_UP
				&& committedDirection != IElevator.ELEVATOR_DIRECTION_UNCOMMITTED) {
			throw new IllegalArgumentException("The commited direction must be 0, 1 or 2!");
		}
		if(this.committedDirection != committedDirection) {
			plc.setCommittedDirection(getNumber(), committedDirection);
			int oldValue = this.committedDirection;
			this.committedDirection = committedDirection;
			this.pcs.firePropertyChange(COMMITTED_DIRECTION_PROPERTY_NAME, oldValue, committedDirection);			
		}
	}

	public int getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(int acceleration) {
		if(this.acceleration != acceleration) {
			int oldValue = this.acceleration;
			this.acceleration = acceleration;
			this.pcs.firePropertyChange(ACCELERATION_PROPERTY_NAME, oldValue, acceleration);				
		}
	}
	
	public boolean getStopRequest(int floor) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor");
		}
		
		return this.stopRequests[floor];
	}
	
	public void setStopRequest(int floor, boolean stop) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor");
		}
		
		if(this.stopRequests[floor] != stop) {
			boolean[] oldValue = Arrays.copyOf(this.stopRequests, this.stopRequests.length);
			this.stopRequests[floor] = stop;
			this.pcs.firePropertyChange(STOP_REQUESTS_PROPERTY_NAME, oldValue, this.stopRequests);
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		if(this.capacity != capacity) {
			int oldValue = this.capacity;
			this.capacity = capacity;
			this.pcs.firePropertyChange(CAPACITY_PROPERTY_NAME, oldValue, capacity);			
		}
	}

	public int getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(int doorStatus) {
		if(this.doorStatus != doorStatus) {
			int oldValue = this.doorStatus;
			this.doorStatus = doorStatus;
			this.pcs.firePropertyChange(DOOR_STATUS_PROPERTY_NAME, oldValue, doorStatus);			
		}
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		if (floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor!");
		}
		if(this.floor != floor) {
			int oldValue = this.floor;
			this.floor = floor;
			this.pcs.firePropertyChange(FLOOR_PROPERTY_NAME, oldValue, floor);			
		}
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) throws RemoteException {
		if (position < 0 || position > numberOfFloors * plc.getFloorHeight()) {
			throw new IllegalArgumentException("Invalid Position!");
		}
		if(this.position != position) {
			int oldValue = this.position;
			this.position = position;
			this.pcs.firePropertyChange(POSITION_PROPERTY_NAME, oldValue, position);			
		}
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		if(this.speed != speed) {
			int oldValue = this.speed;
			this.speed = speed;
			this.pcs.firePropertyChange(SPEED_PROPERTY_NAME, oldValue, speed);			
		}
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		if (weight < 0) {
			throw new IllegalArgumentException("The weight can't be negative!");
		}
		if(this.weight != weight) {
			int oldValue = this.weight;
			this.weight = weight;
			this.pcs.firePropertyChange(WEIGHT_PROPERTY_NAME, oldValue, weight);			
		}
	}
	
	public boolean getServicesFloor(int floor) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor");
		}
		
		return this.servicedFloors[floor];
	}
	
	public void setServicesFloor(int floor, boolean service) throws RemoteException {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor");
		}
		
		if(this.servicedFloors[floor] != service) {
			plc.setServicesFloors(number, floor, service);
			boolean[] oldValue = Arrays.copyOf(this.servicedFloors, this.servicedFloors.length);
			this.servicedFloors[floor] = service;
			this.pcs.firePropertyChange(SERVICED_FLOORS_PROPERTY_NAME, oldValue, this.servicedFloors);	
		}
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) throws RemoteException {
		if (target < 0 || target >= numberOfFloors) {
			throw new IllegalArgumentException("Invalid floor");
		}
		if(this.target != target) {
			plc.setTarget(number, target);
			int oldValue = this.target;
			this.target = target;
			this.pcs.firePropertyChange(TARGET_PROPERTY_NAME, oldValue, target);			
		}
	}

}
