package at.fhhagenberg.sqelevator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
	
public class Floor {
	
	private final IElevator plc;
	private final int number;
	private boolean buttonDown;
	private boolean buttonUp;
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public final static String BUTTON_DOWN_PROPERTY_NAME = "ButtonDown";	
	
	public final static String BUTTON_UP_PROPERTY_NAME = "ButtonUp";	
		
	public Floor(IElevator plc, int number) throws RemoteException {
		this.plc = plc;
		this.number = number;
		
		if(plc == null)
		{
			throw new IllegalArgumentException("Plc must be valid!"); 
		}
		
		this.setButtonDown(plc.getFloorButtonDown(number));
		this.setButtonUp(plc.getFloorButtonUp(number));
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

	public boolean isButtonDown() {
		return buttonDown;
	}

	public void setButtonDown(boolean buttonDown) {
		if(this.buttonDown != buttonDown) {
			boolean oldValue = this.buttonDown;
			this.buttonDown = buttonDown;		
			this.pcs.firePropertyChange(BUTTON_DOWN_PROPERTY_NAME, oldValue, buttonDown);
		}
	}

	public boolean isButtonUp() {
		return buttonUp;
	}

	public void setButtonUp(boolean buttonUp) {
		if(this.buttonUp != buttonUp)
		{
			boolean oldValue = this.buttonUp;
			this.buttonUp = buttonUp;
			this.pcs.firePropertyChange(BUTTON_UP_PROPERTY_NAME, oldValue, buttonUp);
		}
	}

}
