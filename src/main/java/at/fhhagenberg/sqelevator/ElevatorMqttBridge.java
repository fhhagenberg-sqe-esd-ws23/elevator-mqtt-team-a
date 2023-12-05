package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

public class ElevatorMqttBridge implements PropertyChangeListener, IMqttMessageListener {
	
	private final Elevator elevator;
	private final ElevatorsMqttClient mqtt;
	private boolean started = false;

	public ElevatorMqttBridge(Elevator elevator, ElevatorsMqttClient mqtt) {
		this.elevator = elevator;
		this.mqtt = mqtt;
	}
	
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
		}
		
	}

	@Override
	public void setCommittedDirection(int elevator, int direction) {
		if(elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setCommittedDirection(direction);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setServicesFloor(int elevator, int floor, boolean service) {
		if(elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setServicesFloor(floor, service);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setTarget(int elevator, int target) {
		if(elevator == this.elevator.getNumber()) {
			try {
				this.elevator.setTarget(target);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
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
