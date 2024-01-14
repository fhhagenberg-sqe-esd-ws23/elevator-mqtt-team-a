package at.fhhagenberg.sqelevator.algorithm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.Arrays;

import org.w3c.dom.html.HTMLLIElement;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.Elevator;
import sqelevator.IElevator;

public class ElevatorAlgorithm implements PropertyChangeListener {

	private Building mBuilding;
	private AlgorithmMqttAdapter mAdapter;
    //direction in which the elevator will currently try to go
    private final boolean[] mUp;
    private final boolean[] mUpTarget;
    private final boolean[] mDownTarget;
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		switch (evt.getPropertyName()) {
		case Elevator.SERVICED_FLOORS_PROPERTY_NAME: break;
		case Elevator.TARGET_PROPERTY_NAME: break;
		case Elevator.COMMITTED_DIRECTION_PROPERTY_NAME: break;
		default:
			try {
				this.setNextTargets();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ElevatorAlgorithm(AlgorithmMqttAdapter mqttAdapter) {
		
		mAdapter = mqttAdapter;
		mBuilding = mqttAdapter.getBuilding();
        mUp = new boolean[mBuilding.getElevatorCount()];
        Arrays.fill(mUp, true);
        mUpTarget = new boolean[mBuilding.getFloorCount()];
        Arrays.fill(mUpTarget, false);
        mDownTarget = new boolean[mBuilding.getFloorCount()];
        Arrays.fill(mDownTarget, false);
		
		for(int i = 0; i < mBuilding.getElevatorCount(); ++i) {
			mBuilding.getElevators()[i].addPropertyChangeListener(this);
		}
		
		for(int i = 0; i < mBuilding.getFloorCount(); ++i) {
			mBuilding.getFloors()[i].addPropertyChangeListener(this);
		}
	}
	
	
	public void Shutdown() {
		
		for(int i = 0; i < mBuilding.getElevatorCount(); ++i) {
			mBuilding.getElevators()[i].removePropertyChangeListener(this);
		}
		
		for(int i = 0; i < mBuilding.getFloorCount(); ++i) {
			mBuilding.getFloors()[i].removePropertyChangeListener(null);
		}
	}
	
	
    /**
     * determines if an elevator is standing on its target floor
     *
     * @param elevator which to check if it is standing on its target floor
     * @return true if standing on the target floor, false otherwise
     */
    private boolean isOnTargetFloor(Elevator elevator) {
        return elevator.getTarget() == elevator.getFloor() && elevator.getSpeed() == 0 && elevator.getDoorStatus() == IElevator.ELEVATOR_DOORS_OPEN;
    }
	
    /**
     * updates the targets of the elevators that are not already set
     * @throws RemoteException 
     */
    public void setNextTargets() throws RemoteException {

    	/* collect all up/down button presses */
        var upPressed = new boolean[mBuilding.getFloorCount()];
        var downPressed = new boolean[mBuilding.getFloorCount()];
        for (var floor : mBuilding.getFloors()) {
            downPressed[floor.getNumber()] = floor.isButtonDown();
            upPressed[floor.getNumber()] = floor.isButtonUp();
        }

        /* handle the targets one after the other, keep them until the elevator arrives */
        for (var elevator : mBuilding.getElevators()) {
            var nr = elevator.getNumber();

            /* collection of all stops, prevents stopping on current floor */
            var stops = new boolean[mBuilding.getFloorCount()];
            for (int i = 0; i < elevator.getNumberOfFloors(); ++i) {
                stops[i] = elevator.getStopRequest(i);
            }

            if (isOnTargetFloor(elevator)) {
                var currentFloor = elevator.getFloor();

                /* check if this floor was blocked as upwards/downwards target by current elevator */
                if (mUp[nr] && mUpTarget[currentFloor]) {
                    mUpTarget[currentFloor] = false;
                }
                if (!mUp[nr] && mDownTarget[currentFloor]) {
                    mDownTarget[currentFloor] = false;
                }

                /* stopping on current floor not necessary */
                stops[currentFloor] = false;
                downPressed[currentFloor] = false;
                upPressed[currentFloor] = false;

               /* if stops can't be found on the way down, loop again and search upwards */
                var loop = 0;
                while (loop < 2) {
                    loop++;

                    /* if the elevator is searching for stops upwards it starts the search one
                     *  floor above the current, until the last floor is reached */
                    if (mUp[nr]) {

                        int i = currentFloor + 1;
                        for (; i < elevator.getNumberOfFloors(); ++i) {

                            /* if a floor is a requested stop by an inside or outside button it will get serviced
                               when requested outside, make sure that it is not already serviced by another elevator */
                            if (((upPressed[i] && !mUpTarget[i]) || stops[i]) &&
                                    elevator.getServicesFloor(i)) {
                            	mAdapter.setTarget(elevator.getNumber(), i);
                                if (upPressed[i]) {

                                    /* if it was a press from an outside button, this floor is now serviced by
                                       this elevator and other elevators will no longer be able to see the request */
                                    mUpTarget[i] = true;
                                }
                                break;
                            }
                        }

                        /* if no stop was found upwards, we search downwards
                          starting with the upmost floor */
                        if (i == elevator.getNumberOfFloors()) {
                            mUp[nr] = false;
                            if (currentFloor != elevator.getNumberOfFloors() - 1) {
                                currentFloor = i;
                            }
                        }
                        else {
                            break;
                        }
                    }
                    /* repeat the same process as for searching for stops upwards, just on the way down */
                    if (!mUp[nr]) {

                        int i = currentFloor - 1;
                        for (; i >= 0; --i) {
                            if (((downPressed[i] && !mDownTarget[i]) || stops[i]) &&
                                    elevator.getServicesFloor(i)) {
                                mAdapter.setTarget(elevator.getNumber(), i);
                                if (downPressed[i]) {
                                    mDownTarget[i] = true;
                                }
                                break;
                            }
                        }
                        if (i == -1) {
                            mUp[nr] = true;
                            if (currentFloor != 0) {
                                currentFloor = i;
                            }
                        } else {
                            break;
                        }
                    }
                }
                
                /* set direction based on the target */
                if (elevator.getTarget() == elevator.getFloor()) {
                    mAdapter.setCommittedDirection(elevator.getNumber(), IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
                } else if (elevator.getTarget() < elevator.getFloor()) {
                    mAdapter.setCommittedDirection(elevator.getNumber(), IElevator.ELEVATOR_DIRECTION_DOWN);
                } else if (elevator.getTarget() > elevator.getFloor()) {
                    mAdapter.setCommittedDirection(elevator.getNumber(), IElevator.ELEVATOR_DIRECTION_UP);
                }
            }
        }
    }
}
