package at.fhhagenberg.sqelevator.algorithm;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.MqttTopicGenerator;
import sqelevator.IElevator;

public class AlgorithmMqttAdapter implements IElevator {

	private static final  String INVALID_ELEVATOR = "Invalid elevator number!";
	private static final  String INVALID_FLOOR = "Invalid floor!";

	private final int numElevators;
	private final int numFloors;
	private final int floorHeight;
	private boolean isInitialized = false;
	private	Building mBuilding;
	private ElevatorsMqttClient mClient;
	private final MqttTopicGenerator topics = new MqttTopicGenerator();

	public boolean subscribeToStatusMessages() throws InterruptedException, ExecutionException {
		if(!mClient.isConnected()) {
			return false;
		}

		for(int i = 0; i < numElevators; ++i) {
			final int elevator = i;

			if (!mClient.subscribe_int(topics.getAccelerationTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setAcceleration((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getCapacityTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setCapacity((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getDoorsTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setDoorStatus((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getFloorTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setFloor((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getPositionTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setPosition((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getSpeedTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setSpeed((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			if (!mClient.subscribe_int(topics.getWeightTopic(elevator),
					(args, intval)->{
						synchronized(this) {
							mBuilding.getElevators()[elevator].setWeight((int)intval);
						}
					},
					elevator)) {
				return false;
			}

			for(int j = 0; j < numFloors; ++j) {
				final int floor = j;

				if (!mClient.subscribe_int(topics.getButtonTopic(elevator,floor),
						(args, intval)->{
							synchronized(this) {
								mBuilding.getElevators()[elevator].setStopRequest(floor,(int)intval == 1);
							}
						},
						elevator,floor)) {
					return false;
				}

				if (!mClient.subscribe_int(topics.getServicesFloorTopic(elevator,floor),
						(args, intval)->{
							try {
								synchronized(this) {
									mBuilding.getElevators()[elevator].setServicesFloor(floor, (int)intval == 1);
								}
							} catch (RemoteException e) {
								// RemoteException cannot happen here, ignore it
								return;
							}
						},
						elevator,floor)) {
					return false;
				}

				/* only once per floor */
				if(i == 0) {
					if (!mClient.subscribe_int(topics.getButtonDownTopic(floor),
							(args, intval)->{
								synchronized(this) {
									mBuilding.getFloors()[floor].setButtonDown((int)intval == 1);
								}
							},
							floor)) {
						return false;
					}

					if (!mClient.subscribe_int(topics.getButtonUpTopic(elevator),
							(args, intval)->{
								synchronized(this) {
									mBuilding.getFloors()[floor].setButtonUp((int)intval == 1);
								}
							},
							floor)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public AlgorithmMqttAdapter(ElevatorsMqttClient client, int numElevators, int numFloors, int floorHeight) throws RemoteException {
		this.numElevators = numElevators;
		this.numFloors = numFloors;
		this.floorHeight = floorHeight;
		this.mClient = client;
		this.mBuilding = new Building(this);

		for(int i = 0; i < mBuilding.getElevatorCount(); ++i) {
			mBuilding.getElevators()[i].setAlwaysSetPropertyChange(true);
		}

		for(int i = 0; i < mBuilding.getFloorCount(); ++i) {
			mBuilding.getFloors()[i].setAlwaysSetPropertyChange(true);
		}

		isInitialized = true;
	}

	public Building getBuilding()
	{
		return mBuilding;
	}

	@Override
	public int getCommittedDirection(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return IElevator.ELEVATOR_DIRECTION_UNCOMMITTED;

		return mBuilding.getElevators()[elevatorNumber].getCommittedDirection();
	}

	@Override
	public int getElevatorAccel(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

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

		if(!isInitialized) return false;

		return mBuilding.getElevators()[elevatorNumber].getStopRequest(floor);
	}

	@Override
	public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return IElevator.ELEVATOR_DOORS_CLOSED;

		return mBuilding.getElevators()[elevatorNumber].getDoorStatus();
	}

	@Override
	public int getElevatorFloor(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

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

		if(!isInitialized) return 0;

		return mBuilding.getElevators()[elevatorNumber].getPosition();
	}

	@Override
	public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

		return mBuilding.getElevators()[elevatorNumber].getSpeed();
	}

	@Override
	public int getElevatorWeight(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

		return mBuilding.getElevators()[elevatorNumber].getWeight();
	}

	@Override
	public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

		return mBuilding.getElevators()[elevatorNumber].getCapacity();
	}

	@Override
	public boolean getFloorButtonDown(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}

		if(!isInitialized) return false;

		return mBuilding.getFloors()[floor].isButtonDown();
	}

	@Override
	public boolean getFloorButtonUp(int floor) throws RemoteException {		
		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}

		if(!isInitialized) return false;

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

		if(!isInitialized) return false;

		return mBuilding.getElevators()[elevatorNumber].getServicesFloor(floor);
	}

	@Override
	public int getTarget(int elevatorNumber) throws RemoteException {

		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(!isInitialized) return 0;

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

		if(!isInitialized) return;

		mBuilding.getElevators()[elevatorNumber].setCommittedDirection(direction);
		mClient.publishDirectionReceived(elevatorNumber, direction);
	}

	@Override
	public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(floor < 0 || floor >= numFloors) {
			throw new IllegalArgumentException(INVALID_FLOOR);
		}

		if(!isInitialized) return;

		mBuilding.getElevators()[elevatorNumber].setServicesFloor(floor, service);
		mClient.publishServicesFloorReceived(elevatorNumber, floor, service);
	}

	@Override
	public void setTarget(int elevatorNumber, int target) throws RemoteException {
		if(elevatorNumber < 0 || elevatorNumber >= numElevators) {
			throw new IllegalArgumentException(INVALID_ELEVATOR);
		}

		if(target < 0 || target >= numFloors) {
			throw new IllegalArgumentException("Invalid target floor!");
		}

		if(!isInitialized) return;

		mBuilding.getElevators()[elevatorNumber].setTarget(target);
		mClient.publishTargetReceived(elevatorNumber, target);
	}

	@Override
	public long getClockTick() throws RemoteException {
		return 0;
	}
}
