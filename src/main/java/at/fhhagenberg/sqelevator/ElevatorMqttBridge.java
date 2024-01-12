package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

/**
 * Class acting as a bridge between an elevator and a MQTT client.
 * The class implements PropertyChangeListener and listens to changes in the Elevator class.
 * On changes in the elevator class, a MQTT message is published via the MQTT client.
 * The class also implements IMqttMessageListener and listens to control messages coming in via the MQTT client.
 * If the elevator number of the incoming control message matches the associated elevator, the associated elevator is updated accordingly.
 */
public class ElevatorMqttBridge implements IMqttBridge, PropertyChangeListener, IMqttMessageListener {
	
	private final Elevator elevator;
	private final ElevatorsMqttClient mqtt;
	private boolean started = false;

	/**
	 * Create a new Bridge between the given elevator and mqtt client.
	 * @param elevator the elevator to create the bridge for
	 * @param mqtt the mqtt client to create the bridge with
	 */
	public ElevatorMqttBridge(Elevator elevator, ElevatorsMqttClient mqtt) {
		this.elevator = elevator;
		this.mqtt = mqtt;
	}

	/**
	 * Property change method of the implemented PropertyChangeListener interface.
	 * @param evt property change event from the elevator
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() != elevator) {
			return;
		}
		
		switch(evt.getPropertyName()) {
		case Elevator.COMMITTED_DIRECTION_PROPERTY_NAME:
			publishCommittedDirection();
			break;
		case Elevator.ACCELERATION_PROPERTY_NAME:
			publishAcceleration();
			break;
		case Elevator.STOP_REQUESTS_PROPERTY_NAME:
			publishButtonsPressed();
			break;
		case Elevator.CAPACITY_PROPERTY_NAME:
			publishCapacity();
			break;
		case Elevator.DOOR_STATUS_PROPERTY_NAME:
			publishDoorStatus();
			break;
		case Elevator.FLOOR_PROPERTY_NAME:
			publishFloor();
			break;
		case Elevator.POSITION_PROPERTY_NAME:
			publishPosition();
			break;
		case Elevator.SPEED_PROPERTY_NAME:
			publishSpeed();
			break;
		case Elevator.WEIGHT_PROPERTY_NAME:
			publishWeight();
			break;
		case Elevator.SERVICED_FLOORS_PROPERTY_NAME:
			publishServicesFloors();
			break;
		case Elevator.TARGET_PROPERTY_NAME:
			publishTarget();
			break;
		default:
			throw new RuntimeException("Unknown property name.");
		}
		
	}

	/**
	 * Set committed direction method of the implemented IMqttMessageListener interface.
	 * Sets the committed direction of the specified elevator (up / down / uncommitted).
	 * @param elevatorNumber elevator number whose committed direction is being set
	 * @param direction direction being set where up=0, down=1 and uncommitted=2
	 */
	@Override
	public void setCommittedDirection(int elevator, int direction) {
		if(started && elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setCommittedDirection(direction);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set floor service status method of the implemented IMqttMessageListener interface.
	 * Sets whether or not the specified elevator will service the specified floor (yes/no).
	 * @param elevatorNumber elevator number whose service is being defined
	 * @param floor floor whose service by the specified elevator is being set
	 * @param service indicates whether the floor is serviced by the specified elevator (yes=true,no=false)
	 */
	@Override
	public void setServicesFloor(int elevator, int floor, boolean service) {
		if(started && elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setServicesFloor(floor, service);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set target method of the implemented IMqttMessageListener interface.
	 * Sets the floor target of the specified elevator.
	 * @param elevator elevator number whose target floor is being set
	 * @param target floor number which the specified elevator is to target
	 */
	@Override
	public void setTarget(int elevator, int target) {
		if(started && elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setTarget(target);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Start the bridge: Add the property change listener, publish the current information of the elevator and start handling control messages.
	 */
	public void start() {
		if(started) {
			return;
		}
		
		started = true;
		elevator.addPropertyChangeListener(this);
		publishCommittedDirection();
		publishAcceleration();
		publishButtonsPressed();
		publishCapacity();
		publishDoorStatus();
		publishFloor();
		publishPosition();
		publishSpeed();
		publishWeight();
		publishServicesFloors();
		publishTarget();
	}

	/**
	 * Stop the bridge: Remove the property change listener and stop handling control messages.
	 */
	public void stop() {
		started = false;
		elevator.removePropertyChangeListener(this);
	}
	
	private void publishCommittedDirection() {
		mqtt.publishDirection(elevator.getNumber(), elevator.getCommittedDirection());		
	}
	
	private void publishAcceleration() {
		mqtt.publishAcceleration(elevator.getNumber(), elevator.getAcceleration());		
	}
	
	private void publishButtonsPressed() {
		for(int i = 0; i < elevator.getNumberOfFloors(); i++) {
			mqtt.publishButtonPressed(elevator.getNumber(), i, elevator.getStopRequest(i));
		}
	}
	
	private void publishCapacity() {
		mqtt.publishCapacity(elevator.getNumber(), elevator.getCapacity());		
	}

	private void publishDoorStatus() {
		mqtt.publishDoors(elevator.getNumber(), elevator.getDoorStatus());		
	}

	private void publishFloor() {
		mqtt.publishFloor(elevator.getNumber(), elevator.getFloor());		
	}

	private void publishPosition() {
		mqtt.publishPosition(elevator.getNumber(), elevator.getPosition());		
	}

	private void publishSpeed() {
		mqtt.publishSpeed(elevator.getNumber(), elevator.getSpeed());		
	}

	private void publishWeight() {
		mqtt.publishWeight(elevator.getNumber(), elevator.getWeight());		
	}

	private void publishServicesFloors() {
		for(int i = 0; i < elevator.getNumberOfFloors(); i++) {
			mqtt.publishServicesFloor(elevator.getNumber(), i, elevator.getServicesFloor(i));
		}
	}

	private void publishTarget() {
		mqtt.publishTarget(elevator.getNumber(), elevator.getTarget());		
	}
}
