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
    /* direction in which the elevator will currently try to go */
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
     * determines the next targets for the elevators
     *
     * @return void
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
            handleElevatorTargets(elevator, upPressed, downPressed);
        }
    }
    
    
    /* private return class for the searchAndHandleStopsReturn */
    private class searchAndHandleStopsReturn {
    	
    	public int currentfloor;
    	public boolean breakNeeded;
    	
    	public searchAndHandleStopsReturn(int currentfloor, boolean breakNeeded)
    	{
    		this.currentfloor = currentfloor;
    		this.breakNeeded = breakNeeded;
    	}
    }
    

    /**
     * get a new target for the current elevator
     *
     * @param elevator current elevator to handle the targets for
     * @param upPressed current elevator to handle the targets for
     * @return true if standing on the target floor, false otherwise
     */
    
    private void handleElevatorTargets(Elevator elevator, boolean[] upPressed, boolean[] downPressed) throws RemoteException {
      
    	if (isOnTargetFloor(elevator)) {
    		
    		int currentFloor = elevator.getFloor();
    		
    		/* collection of all stops, prevents stopping on current floor */
            var stops = new boolean[mBuilding.getFloorCount()];
            for (int i = 0; i < elevator.getNumberOfFloors(); ++i) {
                stops[i] = elevator.getStopRequest(i);
            }
            
            /* check if this floor was blocked as upwards/downwards target by current elevator */
            if (mUp[elevator.getNumber()] && mUpTarget[currentFloor]) {
                mUpTarget[currentFloor] = false;
            }
            if (!mUp[elevator.getNumber()] && mDownTarget[currentFloor]) {
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
            	
	            /* Handle stops and requests upwards */
	            if (mUp[elevator.getNumber()]) {
	            	searchAndHandleStopsReturn ret = searchAndHandleStops(elevator, currentFloor, upPressed, mUpTarget, stops, true);
	            
	            	 /* if no stop was found upwards, we search downwards
                    starting with the upmost floor */
	            	currentFloor = ret.currentfloor;
	            	if(ret.breakNeeded) {
	            		break;
	            	}
	            }
	            	
	            /* If stops were not found upwards, search downwards */
	            if (!mUp[elevator.getNumber()]) {
	            	searchAndHandleStopsReturn ret = searchAndHandleStops(elevator, currentFloor, downPressed, mDownTarget, stops, false);
	            	
	            	currentFloor = ret.currentfloor;
	            	if(ret.breakNeeded) {
	            		break;
	            	}
	            }
            }
            
            updateDirectionAndClearTargets(elevator);
        }
    }


    private searchAndHandleStopsReturn searchAndHandleStops(Elevator elevator, int currentFloor, boolean[] pressedArray, boolean[] targetArray, boolean[] stops, boolean isUpwards) throws RemoteException {
        int floorIterator = isUpwards ? currentFloor + 1 : currentFloor - 1;
        boolean breakRequest = false;
        
        while ((isUpwards && floorIterator < elevator.getNumberOfFloors()) || (!isUpwards && floorIterator >= 0)) {
            if (handleFloorStopRequest(elevator, floorIterator, pressedArray, targetArray, stops)) {
                break;
            }

            floorIterator += isUpwards ? 1 : -1;
        }
        
        if(isUpwards && floorIterator == elevator.getNumberOfFloors())
        {
            mUp[elevator.getNumber()] = false;
            if (currentFloor != elevator.getNumberOfFloors() - 1) {
                currentFloor = floorIterator;
            }
            
        }
        else if (!isUpwards &&  floorIterator == -1)
        {
            mUp[elevator.getNumber()] = true;
            if (currentFloor != 0) {
                currentFloor = floorIterator;
            }
        }
        else
        {
        	breakRequest = true;
        }
        
        return new searchAndHandleStopsReturn(currentFloor,breakRequest);
    }

    private boolean handleFloorStopRequest(Elevator elevator, int floor, boolean[] pressedArray, boolean[] targetArray, boolean[] stops) throws RemoteException {
    	if (((pressedArray[floor] && !targetArray[floor]) || stops[floor]) &&
                elevator.getServicesFloor(floor)) {
            mAdapter.setTarget(elevator.getNumber(), floor);
            if (pressedArray[floor]) {
                targetArray[floor] = true;
            }
            return true;
        }
        return false;
    }
    
    private void updateDirectionAndClearTargets(Elevator elevator) throws RemoteException {
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
