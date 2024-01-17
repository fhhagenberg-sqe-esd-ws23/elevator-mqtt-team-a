package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

import sqelevator.IElevator;

/**
 * Class representing a floor in a building.
 * The floor is initialized in the constructor using information from the control unit provided.
 * Every floor has a unique number.
 * This class implements PropertyChangeSupport and lets PropertyChangeListeners listen to property changes.
 */
public class Floor {

	private final IElevator plc;
	private final int number;
	private boolean buttonDown;
	private boolean buttonUp;
	private boolean alwaysCallPropertyChange = false;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/** Name of property ButtonDown. */
	public static final String BUTTON_DOWN_PROPERTY_NAME = "ButtonDown";

	/** Name of property ButtonUp. */
	public static final String BUTTON_UP_PROPERTY_NAME = "ButtonUp";

	/**
	 * Create a new Floor object from the given IElevator API.
	 * @param plc the IElevator API to create the floor from
	 * @param number the unique number associated with the floor
	 * @throws RemoteException if the connection to the IElevator API is lost
	 */
	public Floor(IElevator plc, int number) throws RemoteException {
		this.plc = plc;
		this.number = number;
	
		if(plc == null) {
			throw new IllegalArgumentException("Plc must be valid!"); 
		}

		this.setButtonDown(plc.getFloorButtonDown(number));
		this.setButtonUp(plc.getFloorButtonUp(number));
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
	 * Provides the elevator PLC connection object the floor was created from.
	 * @return the elevator PLC connection object
	 */
	public IElevator getPlc() {
		return plc;
	}

	/**
	 * Provides the unique number of the floor.
	 * @return the unique number of the floor
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Provides the status of the Down button on the floor (on/off).
	 * @return returns boolean to indicate if button is active (true) or not (false)
	 */
	public boolean isButtonDown() {
		return buttonDown;
	}

	/**
	 * Sets the status of the Down button on the floor (on/off).
	 * @param buttonDown boolean to indicate if button is active (true) or not (false)
	 */
	public void setButtonDown(boolean buttonDown) {
		if(alwaysCallPropertyChange || this.buttonDown != buttonDown) {
			boolean oldValue = this.buttonDown;
			this.buttonDown = buttonDown;
			if(alwaysCallPropertyChange) {
				oldValue = !buttonDown;
			}
			this.pcs.firePropertyChange(BUTTON_DOWN_PROPERTY_NAME, oldValue, buttonDown);
		}
	}

	/**
	 * Provides the status of the Up button on the floor (on/off).
	 * @return returns boolean to indicate if button is active (true) or not (false)
	 */
	public boolean isButtonUp() {
		return buttonUp;
	}

	/**
	 * Sets the status of the Up button on the floor (on/off).
	 * @param buttonUp boolean to indicate if button is active (true) or not (false)
	 */
	public void setButtonUp(boolean buttonUp) {
		if(alwaysCallPropertyChange || this.buttonUp != buttonUp) {
			boolean oldValue = this.buttonUp;
			this.buttonUp = buttonUp;
			if(alwaysCallPropertyChange) {
				oldValue = !buttonUp;
			}
			this.pcs.firePropertyChange(BUTTON_UP_PROPERTY_NAME, oldValue, buttonUp);
		}
	}
	
	public void setAlwaysSetPropertyChange(boolean set) {
		alwaysCallPropertyChange = set;
	}

}
