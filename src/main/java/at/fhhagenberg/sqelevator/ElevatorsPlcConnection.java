package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import sqelevator.IElevator;

public class ElevatorsPlcConnection implements IElevator {
	
	private final ElevatorProperties props;
	private IElevator plc;
	
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
	
	public boolean connect(OutputStream output) {
		try {
			Registry registry = LocateRegistry.getRegistry(props.getRmiAddress(), props.getRmiPort());
			
			try {
				plc = (IElevator) registry.lookup(props.getRmiName());
				return true;
			}
			catch(NotBoundException e) {
				try {
					OutputStreamWriter writer = new OutputStreamWriter(output);
					writer.write("Name not found during registry lookup: ");
					writer.write(e.getMessage());
					writer.write("\nRetrying ...\n");
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			catch(ServerException | AccessException e) {
				try {
					OutputStreamWriter writer = new OutputStreamWriter(output);
					writer.write("Access denied during registry lookup: ");
					writer.write(e.getMessage());
					writer.write("\nRetrying ...\n");
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			catch(RemoteException e) {
				try {
					OutputStreamWriter writer = new OutputStreamWriter(output);
					writer.write("Error during registry lookup: ");
					writer.write(e.getMessage());
					writer.write("\nRetrying ...\n");
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		catch(RemoteException e) {
			try {
				OutputStreamWriter writer = new OutputStreamWriter(output);
				writer.write("Error retrieving registry: ");
				writer.write(e.getMessage());
				writer.write("\nRetrying ...\n");
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return false;
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
