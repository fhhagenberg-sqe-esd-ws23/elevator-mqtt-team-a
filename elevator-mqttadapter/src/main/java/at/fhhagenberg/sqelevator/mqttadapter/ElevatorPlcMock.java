package at.fhhagenberg.sqelevator.mqttadapter;

import java.rmi.RemoteException;

import sqelevator.IElevator;

/**
 * Class provides a mock object for the IElevator interface to run the application without the elevator simulator.
 * All getters return 0 by default.
 * Values set by the setters are stored so the associated getters can return different values.
 */
public class ElevatorPlcMock implements IElevator {

	private static final String INVALID_ELEVATOR = "Invalid elevator number!";
	private static final String INVALID_FLOOR = "Invalid floor!";
	
	private final int numElevators;
	private final int numFloors;
	private final int floorHeight;
	private final int[] committedDirection;
	private final int[] accel;
	private final boolean[][] buttons;
	private final int[] doorStatus;
	private final int[] floor;
	private final int[] position;
	private final int[] speed;
	private final int[] weight;
	private final int[] capacity;
	private final boolean[] floorButtonDown;
	private final boolean[] floorButtonUp;
	private final int[] target;
	private final boolean[][] servicedFloors;

	/**
	 * Create a new IElevator mock object with the given building parameters.
	 * @param numElevators number of elevators in the mock building
	 * @param numFloors number of floors in the mock building
	 * @param floorHeight height of the floors in the mock building
	 */
	public ElevatorPlcMock(int numElevators, int numFloors, int floorHeight) {
		this.numElevators = numElevators;
		this.numFloors = numFloors;
		this.floorHeight = floorHeight;
		committedDirection = new int[numElevators];
		accel = new int[numElevators];
		buttons = new boolean[numElevators][numFloors];
		doorStatus = new int[numElevators];
		floor = new int[numElevators];
		position = new int[numElevators];
		speed = new int[numElevators];
		weight = new int[numElevators];
		capacity = new int[numElevators];
		floorButtonDown = new boolean[numFloors];
		floorButtonUp = new boolean[numFloors];
		target = new int[numElevators];
		servicedFloors = new boolean[numElevators][numFloors];
		
		for(int i = 0; i < numElevators; ++i) {
			doorStatus[i] = IElevator.ELEVATOR_DOORS_CLOSED;
		}
	}

	@Override
	public int getCommittedDirection(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException();
		}
		
		return committedDirection[elevatorNumber];
	}

	@Override
	public int getElevatorAccel(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return accel[elevatorNumber];
	}

	@Override
	public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return buttons[elevatorNumber][floor];
	}

	@Override
	public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return doorStatus[elevatorNumber];
	}

	@Override
	public int getElevatorFloor(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return floor[elevatorNumber];
	}

	@Override
	public int getElevatorNum() throws RemoteException {
		return numElevators;
	}

	@Override
	public int getElevatorPosition(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return position[elevatorNumber];
	}

	@Override
	public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return speed[elevatorNumber];
	}

	@Override
	public int getElevatorWeight(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return weight[elevatorNumber];
	}

	@Override
	public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return capacity[elevatorNumber];
	}

	@Override
	public boolean getFloorButtonDown(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return floorButtonDown[floor];
	}

	@Override
	public boolean getFloorButtonUp(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return floorButtonUp[floor];
	}

	@Override
	public int getFloorHeight() throws RemoteException {
		return floorHeight;
	}

	@Override
	public int getFloorNum() throws RemoteException {
		return numFloors;
	}

	@Override
	public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return servicedFloors[elevatorNumber][floor];
	}

	@Override
	public int getTarget(int elevatorNumber) throws RemoteException {
		return target[elevatorNumber];
	}

	@Override
	public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(direction < IElevator.ELEVATOR_DIRECTION_UP || direction > IElevator.ELEVATOR_DIRECTION_UNCOMMITTED) {
			throw new IllegalArgumentException("Invalid direction!");
		}
		
		committedDirection[elevatorNumber] = direction;
	}

	@Override
	public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		servicedFloors[elevatorNumber][floor] = service;
	}

	@Override
	public void setTarget(int elevatorNumber, int target) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(target < 0 || target >= numFloors) {
			throw new IllegalArgumentException("Invalid target floor!");
		}
		
		this.target[elevatorNumber] = target;
	}

	@Override
	public long getClockTick() throws RemoteException {
		return 0;
	}

}
