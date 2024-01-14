package at.fhhagenberg.sqelevator.algorithm;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.MqttTopicGenerator;
import sqelevator.IElevator;

public class AlgorithmMqttAdapter implements IElevator {

	private final static String INVALID_ELEVATOR = "Invalid elevator number!";
	private final static String INVALID_FLOOR = "Invalid floor!";
	
	private final int numElevators;
	private final int numFloors;
	private final int floorHeight;
	private	Building mBuilding;
	private ElevatorsMqttClient mClient;
	private final MqttTopicGenerator topics = new MqttTopicGenerator();
	
	private boolean subscribeToStatusMessages() throws InterruptedException, ExecutionException {
		
		if(!mClient.isConnected()) {
			return false;
		}
		
				
		for(int i = 0; i < numElevators; ++i) {
			final int elevator = i;
			
			if (!mClient.subscribe_int(topics.getAccelerationTopic(elevator),
								(args, intval)->{
									mBuilding.getElevators()[(int)args[0]].setAcceleration((int)intval);},
								elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getCapacityTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setCapacity((int)intval);},
					elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getDoorsTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setDoorStatus((int)intval);},
					elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getFloorTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setFloor((int)intval);},
					elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getPositionTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setPosition((int)intval);},
					elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getSpeedTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setSpeed((int)intval);},
					elevator)) {
				return false;
			}
			
			if (!mClient.subscribe_int(topics.getWeightTopic(elevator),
					(args, intval)->{
						mBuilding.getElevators()[(int)args[0]].setWeight((int)intval);},
					elevator)) {
				return false;
			}
						
			
			for(int j = 0; j < numFloors; ++j) {
				final int floor = j;
				
				
				if (!mClient.subscribe_int(topics.getButtonTopic(elevator,floor),
						(args, intval)->{
							mBuilding.getElevators()[(int)args[0]].setStopRequest((int)args[1],(int)intval == 1);},
						elevator,floor)) {
					return false;
				}
				
				
				/* only once per floor */
				if(j == 0) {
					if (!mClient.subscribe_int(topics.getButtonDownTopic(floor),
							(args, intval)->{
								mBuilding.getFloors()[(int)args[0]].setButtonDown((int)intval == 1);},
							floor)) {
						return false;
					}
					
					if (!mClient.subscribe_int(topics.getButtonUpTopic(elevator),
							(args, intval)->{
								mBuilding.getFloors()[(int)args[0]].setButtonUp((int)intval == 1);},
							floor)) {
						return false;
					}
				}
				
			}
		}
		return true;
	}
	
	public AlgorithmMqttAdapter(ElevatorsMqttClient client, int numElevators, int numFloors, int floorHeight) throws RemoteException, InterruptedException, ExecutionException {
		this.numElevators = numElevators;
		this.numFloors = numFloors;
		this.floorHeight = floorHeight;
		this.mClient = client;
		this.mBuilding = new Building(this);

		if(!subscribeToStatusMessages())
		{
			throw new RuntimeException("could not subscribe to all status messages");
		}
	}
	
	public Building getBuilding()
	{
		return mBuilding;
	}
	
	@Override
	public int getCommittedDirection(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException();
		}
		
		return mBuilding.getElevators()[elevatorNumber].getCommittedDirection();
	}

	@Override
	public int getElevatorAccel(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getAcceleration();
	}

	@Override
	public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getStopRequest(floor);
	}

	@Override
	public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getDoorStatus();
	}

	@Override
	public int getElevatorFloor(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getFloor();
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
		
		return mBuilding.getElevators()[elevatorNumber].getPosition();
	}

	@Override
	public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getSpeed();
	}

	@Override
	public int getElevatorWeight(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getWeight();
	}

	@Override
	public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getCapacity();
	}

	@Override
	public boolean getFloorButtonDown(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return mBuilding.getFloors()[floor].isButtonDown();
	}

	@Override
	public boolean getFloorButtonUp(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		
		return mBuilding.getFloors()[floor].isButtonUp();
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
		
		
		return mBuilding.getElevators()[elevatorNumber].getServicesFloor(floor);
	}

	@Override
	public int getTarget(int elevatorNumber) throws RemoteException {
		
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		return mBuilding.getElevators()[elevatorNumber].getTarget();
	}

	@Override
	public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(direction < IElevator.ELEVATOR_DIRECTION_UP || direction > IElevator.ELEVATOR_DIRECTION_UNCOMMITTED) {
			throw new IllegalArgumentException("Invalid direction!");
		}
		
		mBuilding.getElevators()[elevatorNumber].setCommittedDirection(direction);
		mClient.publishDirection(elevatorNumber, direction);
	}

	@Override
	public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}
		mBuilding.getElevators()[elevatorNumber].setServicesFloor(floor, service);
		mClient.publishServicesFloor(elevatorNumber, floor, service);
	}

	@Override
	public void setTarget(int elevatorNumber, int target) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}
		
		if(target < 0 || target >= numFloors) {
			throw new IllegalArgumentException("Invalid target floor!");
		}
		
		mBuilding.getElevators()[elevatorNumber].setTarget(target);
		mClient.publishTarget(elevatorNumber, target);
	}

	@Override
	public long getClockTick() throws RemoteException {
		return 0;
	}

}
