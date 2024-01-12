package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Class acting as a bridge between a floor and a MQTT client.
 * The class implements PropertyChangeListener and listens to changes in the Floor class.
 * On changes in the floor class, a MQTT message is published via the MQTT client.
 */
public class FloorMqttBridge implements IMqttBridge, PropertyChangeListener {

	private final Floor floor;
	private final ElevatorsMqttClient mqtt;
	private boolean started = false;

	/**
	 * Create a new Bridge between the given floor and mqtt client.
	 * @param floor the floor to create the bridge for
	 * @param mqtt the mqtt client to create the bridge with
	 */
	public FloorMqttBridge(Floor floor, ElevatorsMqttClient mqtt) {
		this.floor = floor;
		this.mqtt = mqtt;
	}

	/**
	 * Property change method of the implemented PropertyChangeListener interface.
	 * @param evt property change event from the floor
	 */
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
		default:
			throw new RuntimeException("Unknown property name.");
		}
	}

	/**
	 * Start the bridge: Add the property change listener and publish the current information of the elevator..
	 */
	public void start() {
		if(started) {
			return;
		}
		
		started = true;
		floor.addPropertyChangeListener(this);
		publishButtonUp();
		publishButtonDown();
	}

	/**
	 * Stop the bridge: Remove the property change listener.
	 */
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
