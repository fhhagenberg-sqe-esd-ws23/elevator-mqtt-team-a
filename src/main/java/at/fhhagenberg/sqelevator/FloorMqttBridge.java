package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FloorMqttBridge implements PropertyChangeListener {

	private final Floor floor;
	private final ElevatorsMqttClient mqtt;
	private boolean started = false;

	public FloorMqttBridge(Floor floor, ElevatorsMqttClient mqtt) {
		this.floor = floor;
		this.mqtt = mqtt;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() != floor) {
			return;
		}
		
		switch(evt.getPropertyName()) {
		case Floor.BUTTON_UP_PROPERTY_NAME:
			publishButtonUp();
			break;
		case Floor.BUTTON_DOWN_PROPERTY_NAME:
			publishButtonDown();
			break;
		}
	}
	
	public void start() {
		if(started) {
			return;
		}
		
		started = true;
		floor.addPropertyChangeListener(this);
		publishButtonUp();
		publishButtonDown();
	}
	
	public void stop() {
		started = false;
		floor.removePropertyChangeListener(this);
	}
	
	private void publishButtonUp() {
		mqtt.publishButtonUp(floor.getNumber(), floor.isButtonUp());		
	}

	private void publishButtonDown() {
		mqtt.publishButtonDown(floor.getNumber(), floor.isButtonDown());		
	}

}
