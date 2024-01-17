package at.fhhagenberg.sqelevator.mqttadapter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import at.fhhagenberg.sqelevator.ElevatorProperties;
import sqelevator.IElevator;

/**
 * Wrapper for remote IElevator references.
 * 
 * After a connection loss registry.lookup() returns a different IElevator reference object at reconnect.
 * This class provides reusable objects which forward method calls to the current IElevator object.
 * 
 * All IElevator methods throw a RuntimeException if connect() wasn't successful at least once before calling them.
 */
public class ElevatorsPlcConnection implements IElevator {

	private final ElevatorProperties props;
	private IElevator plc;

	/**
	 * Create new IElevator connection object.
	 * @param props the properties with the connection information for the remote API
	 */
	public ElevatorsPlcConnection(ElevatorProperties props) {
		if(props == null) {
			throw new IllegalArgumentException("ElevatorProperties must not be null!");
		}
		
		this.props = props;
	}

	private IElevator getPlc() {
		if(plc == null) {
			throw new RuntimeException("Connect method must be successful once before using other methods!");
		}
		
		return plc;
	}

	/**
	 * Connect to the remote IElevator API.
	 * @param output output stream for displaying information
	 * @return whether the connection to the IElevator API was successful (true) or not (false)
	 */
	public boolean connect(OutputStream output) {
		if(output == null) {
			throw new IllegalArgumentException("Output stream must not be null!");
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(props.getRmiAddress(), props.getRmiPort());
			return lookupRemoteObject(registry, output);
		}
		catch(RemoteException e) {
			writeErrorOutput(output, e, "Error retrieving registry: ");
		}
		
		return false;
	}

	private boolean lookupRemoteObject(Registry registry, OutputStream output) {
		assert(registry != null);
		assert(output != null);
		
		try {
			plc = (IElevator) registry.lookup(props.getRmiName());
			return true;
		}
		catch(NotBoundException e) {
			writeErrorOutput(output, e, "Name not found during registry lookup: ");
		}
		catch(ServerException | AccessException e) {
			writeErrorOutput(output, e, "Access denied during registry lookup: ");
		}
		catch(RemoteException e) {
			writeErrorOutput(output, e, "Error during registry lookup: ");
		}
		
		return false;
	}

	private void writeErrorOutput(OutputStream output, Exception e, String label) {
		assert(label != null);
		
		try {
			OutputStreamWriter writer = new OutputStreamWriter(output);
			writer.write(label);
			writer.write(e.getMessage());
			writer.flush();
		} catch (IOException e1) {
		}
	}

	@Override
	public int getCommittedDirection(int elevatorNumber) throws RemoteException {
		return getPlc().getCommittedDirection(elevatorNumber);
	}

	@Override
	public int getElevatorAccel(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorAccel(elevatorNumber);
	}

	@Override
	public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
		return getPlc().getElevatorButton(elevatorNumber, floor);
	}

	@Override
	public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorDoorStatus(elevatorNumber);
	}

	@Override
	public int getElevatorFloor(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorFloor(elevatorNumber);
	}

	@Override
	public int getElevatorNum() throws RemoteException {
		return getPlc().getElevatorNum();
	}

	@Override
	public int getElevatorPosition(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorPosition(elevatorNumber);
	}

	@Override
	public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorSpeed(elevatorNumber);
	}

	@Override
	public int getElevatorWeight(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorWeight(elevatorNumber);
	}

	@Override
	public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
		return getPlc().getElevatorCapacity(elevatorNumber);
	}

	@Override
	public boolean getFloorButtonDown(int floor) throws RemoteException {
		return getPlc().getFloorButtonDown(floor);
	}

	@Override
	public boolean getFloorButtonUp(int floor) throws RemoteException {
		return getPlc().getFloorButtonUp(floor);
	}

	@Override
	public int getFloorHeight() throws RemoteException {
		return getPlc().getFloorHeight();
	}

	@Override
	public int getFloorNum() throws RemoteException {
		return getPlc().getFloorNum();
	}

	@Override
	public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
		return getPlc().getServicesFloors(elevatorNumber, floor);
	}

	@Override
	public int getTarget(int elevatorNumber) throws RemoteException {
		return getPlc().getTarget(elevatorNumber);
	}

	@Override
	public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
		getPlc().setCommittedDirection(elevatorNumber, direction);
	}

	@Override
	public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
		getPlc().setServicesFloors(elevatorNumber, floor, service);
	}

	@Override
	public void setTarget(int elevatorNumber, int target) throws RemoteException {
		getPlc().setTarget(elevatorNumber, target);
	}

	@Override
	public long getClockTick() throws RemoteException {
		return getPlc().getClockTick();
	}

}
