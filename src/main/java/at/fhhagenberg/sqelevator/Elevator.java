package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.Arrays;

import sqelevator.IElevator;

/**
 * Class representing an elevator in a building.
 * The elevator is initialized in the constructor using information from the control unit provided.
 * Every elevator has a unique number.
 * This class implements PropertyChangeSupport and lets PropertyChangeListeners listen to property changes.
 */
public class Elevator {
	
	private final static String INVALID_FLOOR = "Invalid floor!";

	private final IElevator plc;
	private final int number;
	private final int numberOfFloors;
	private final int floorHeight;
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

	/** Name of property CommittedDirection. */
	public final static String COMMITTED_DIRECTION_PROPERTY_NAME = "CommittedDirection";

	/** Name of property Acceleration. */
	public final static String ACCELERATION_PROPERTY_NAME = "Acceleration";

	/** Name of property StopRequests. */
	public final static String STOP_REQUESTS_PROPERTY_NAME = "StopRequests";

	/** Name of property Capacity. */
	public final static String CAPACITY_PROPERTY_NAME = "Capacity";

	/** Name of property DoorStatus. */
	public final static String DOOR_STATUS_PROPERTY_NAME = "DoorStatus";

	/** Name of property Floor. */
	public final static String FLOOR_PROPERTY_NAME = "Floor";

	/** Name of property Position. */
	public final static String POSITION_PROPERTY_NAME = "Position";

	/** Name of property Speed. */
	public final static String SPEED_PROPERTY_NAME = "Speed";

	/** Name of property Weight. */
	public final static String WEIGHT_PROPERTY_NAME = "Weight";

	/** Name of property ServicedFloors. */
	public final static String SERVICED_FLOORS_PROPERTY_NAME = "ServicedFloors";

	/** Name of property Target. */
	public final static String TARGET_PROPERTY_NAME = "Target";

	/**
	 * Create a new Elevator object from the given IElevator API.
	 * @param plc the IElevator API to create the elevator from
	 * @param number the unique number associated with the elevator
	 * @throws RemoteException if the connection to the IElevator API is lost
	 */
	public Elevator(IElevator plc, int number) throws RemoteException {
		if(plc == null) {
			throw new IllegalArgumentException("Plc must be valid!"); 
		}
		if (number < 0) {
			throw new IllegalArgumentException("The number of the Elevator must be >=0!");
		}
		
		this.plc = plc;
		this.number = number;
		numberOfFloors = plc.getFloorNum();
		floorHeight = plc.getFloorHeight();
		
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

	/**
	 * Adds a property change listener.
	 * @param listener property change listener to add
	 */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
	 * Removes a property change listener.
	 * @param listener property change listener to remove
	 */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
	 * Provides the elevator PLC connection object the elevator was created from.
	 * @return the elevator PLC connection object
	 */
	public IElevator getPlc() {
		return plc;
	}

	/**
	 * Provides the unique number of the elevator.
	 * @return the unique number of the elevator
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Retrieves the committed direction of the elevator (up / down / uncommitted).
	 * @return the current direction of the elevator where up=0, down=1 and uncommitted=2
	 */
	public int getCommittedDirection() {
		return committedDirection;
	}
	
	/**
	 * Retrieves the number of floors in the building where the elevator is located. 
	 * @return total number of floors in the building where the elevator is located
	 */
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	/**
	 * Sets the committed direction of the elevator (up / down / uncommitted).
	 * The committed direction will be set in the PLC as well if differs from the currently stored one.
	 * @param committedDirection direction being set where up=0, down=1 and uncommitted=2
	 */
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

	/**
	 * Provides the current acceleration of the elevator in feet per sec^2.
	 * @return returns the acceleration of the elevator where positive speed is acceleration and negative is deceleration
	 */
	public int getAcceleration() {
		return acceleration;
	}

	/**
	 * Sets the current acceleration of the elevator in feet per sec^2.
	 * @param committedDirection the acceleration of the elevator where positive speed is acceleration and negative is deceleration
	 */
	public void setAcceleration(int acceleration) {
		if(this.acceleration != acceleration) {
			int oldValue = this.acceleration;
			this.acceleration = acceleration;
			this.pcs.firePropertyChange(ACCELERATION_PROPERTY_NAME, oldValue, acceleration);				
		}
	}
	
	/**
	 * Provides the status of a floor request button on the elevator (on/off).
	 * @param floor floor number button being checked on the elevator
	 * @return returns boolean to indicate if floor button on the elevator is active (true) or not (false)
	 */
	public boolean getStopRequest(int floor) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return this.stopRequests[floor];
	}
	
	/**
	 * Sets the status of a floor request button on the elevator (on/off).
	 * @param floor floor number button being checked on the elevator
	 * @param stop boolean to indicate if floor button on the elevator is active (true) or not (false)
	 */
	public void setStopRequest(int floor, boolean stop) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		if(this.stopRequests[floor] != stop) {
			boolean[] oldValue = Arrays.copyOf(this.stopRequests, this.stopRequests.length);
			this.stopRequests[floor] = stop;
			this.pcs.firePropertyChange(STOP_REQUESTS_PROPERTY_NAME, oldValue, this.stopRequests);
		}
	}

	/**
	 * Retrieves the maximum number of passengers that can fit on the elevator.
	 * @return maximum number of passengers
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the maximum number of passengers that can fit on the elevator.
	 * @param capacity maximum number of passengers
	 */
	public void setCapacity(int capacity) {
		if(this.capacity != capacity) {
			int oldValue = this.capacity;
			this.capacity = capacity;
			this.pcs.firePropertyChange(CAPACITY_PROPERTY_NAME, oldValue, capacity);			
		}
	}

	/**
	 * Provides the current status of the doors of the elevator (open/closed).
	 * @return returns the door status of the elevator where 1=open and 2=closed
	 */
	public int getDoorStatus() {
		return doorStatus;
	}

	/**
	 * Sets the current status of the doors of the elevator (open/closed).
	 * @param doorStatus the door status of the elevator where 1=open and 2=closed
	 */
	public void setDoorStatus(int doorStatus) {
		if(doorStatus != IElevator.ELEVATOR_DOORS_OPEN &&
				doorStatus != IElevator.ELEVATOR_DOORS_CLOSED) {
			throw new IllegalArgumentException("Invalid door status");
		}
		
		if(this.doorStatus != doorStatus) {
			int oldValue = this.doorStatus;
			this.doorStatus = doorStatus;
			this.pcs.firePropertyChange(DOOR_STATUS_PROPERTY_NAME, oldValue, doorStatus);			
		}
	}

	/**
	 * Provides the current location of the elevator to the nearest floor 
	 * @return returns the floor number of the floor closest to the elevator
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Sets the current location of the elevator to the nearest floor 
	 * @param floor the floor number of the floor closest to the elevator
	 */
	public void setFloor(int floor) {
		if (floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		if(this.floor != floor) {
			int oldValue = this.floor;
			this.floor = floor;
			this.pcs.firePropertyChange(FLOOR_PROPERTY_NAME, oldValue, floor);			
		}
	}

	/**
	 * Provides the current location of the elevator in feet from the bottom of the building.
	 * @return returns the location in feet of the elevator from the bottom of the building
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the current location of the elevator in feet from the bottom of the building.
	 * @param position the location in feet of the elevator from the bottom of the building
	 */
	public void setPosition(int position) {
		if (position < 0 || position > numberOfFloors * floorHeight) {
			throw new IllegalArgumentException("Invalid Position!");
		}
		if(this.position != position) {
			int oldValue = this.position;
			this.position = position;
			this.pcs.firePropertyChange(POSITION_PROPERTY_NAME, oldValue, position);			
		}
	}

	/**
	 * Provides the current speed of the elevator in feet per sec.
	 * @return returns the speed of the elevator where positive speed is up and negative is down
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets the current speed of the elevator in feet per sec.
	 * @param speed the speed of the elevator where positive speed is up and negative is down
	 */
	public void setSpeed(int speed) {
		if(this.speed != speed) {
			int oldValue = this.speed;
			this.speed = speed;
			this.pcs.firePropertyChange(SPEED_PROPERTY_NAME, oldValue, speed);			
		}
	}

	/**
	 * Retrieves the weight of passengers on the elevator.
	 * @return total weight of all passengers in lbs
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Sets the weight of passengers on the elevator.
	 * @param weight total weight of all passengers in lbs
	 */
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
	
	/** 
	 * Retrieves whether or not the elevator will service the specified floor (yes/no).
	 * @param floor floor whose service status by the specified elevator is being retrieved
	 * @return service status whether the floor is serviced by the elevator (yes=true,no=false)
	 */
	public boolean getServicesFloor(int floor) {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return this.servicedFloors[floor];
	}

	/** 
	 * Sets whether or not the elevator will service the specified floor (yes/no).
	 * The floor service status will be set in the PLC as well if differs from the currently stored one.
	 * @param floor floor whose service status by the specified elevator is being set
	 * @param service new service status whether the floor is serviced by the elevator (yes=true,no=false)
	 */
	public void setServicesFloor(int floor, boolean service) throws RemoteException {
		if(floor < 0 || floor >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		if(this.servicedFloors[floor] != service) {
			plc.setServicesFloors(number, floor, service);
			boolean[] oldValue = Arrays.copyOf(this.servicedFloors, this.servicedFloors.length);
			this.servicedFloors[floor] = service;
			this.pcs.firePropertyChange(SERVICED_FLOORS_PROPERTY_NAME, oldValue, this.servicedFloors);	
		}
	}

	/**
	 * Retrieves the floor target of the elevator.
	 * @return current floor target of the elevator
	 */
	public int getTarget() {
		return target;
	}

	/**
	 * Sets the floor target of the elevator.
	 * The target will be set in the PLC as well if differs from the currently stored one.
	 * @param target new floor target of the elevator
	 */
	public void setTarget(int target) throws RemoteException {
		if (target < 0 || target >= numberOfFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		if(this.target != target) {
			plc.setTarget(number, target);
			int oldValue = this.target;
			this.target = target;
			this.pcs.firePropertyChange(TARGET_PROPERTY_NAME, oldValue, target);			
		}
	}

}
