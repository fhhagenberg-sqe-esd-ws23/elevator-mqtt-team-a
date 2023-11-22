package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Arrays;

public class ElevatorsMqttAdapter {
	private final IUpdater[] updaters;
	private long updateTimerPeriodMs = 250;
	private String exitLine = "exit";

	public ElevatorsMqttAdapter(Building building) throws RemoteException {
		Elevator[] elevators = building.getElevators();
		Floor[] floors = building.getFloors();
		updaters = new IUpdater[elevators.length + floors.length];
		
		for(int i = 0; i < elevators.length; ++i) {
			updaters[i] = new ElevatorUpdater(elevators[i]);
		}
		
		for(int i = 0; i < floors.length; ++i) {
			updaters[elevators.length + i] = new FloorUpdater(floors[i]);
		}
	}
	
	public void run(InputStream input, OutputStream output) throws InterruptedException, IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		writer.write("Started Elevators Mqtt Adapter.\n");
		writer.write("Enter \"" + exitLine + "\" to stop the application.\n");
		writer.flush();
		
		InputStreamThread thread = new InputStreamThread(input, exitLine);
		thread.start();
		
		while(true) {
			Thread.sleep(updateTimerPeriodMs);
			
			if(thread.isExitRequest()) {
				thread.join();
				writer.write("Exited on user request.\n");
				writer.flush();
				return;
			}
			
			for(IUpdater updater : updaters) {
				updater.update();
			}
		}
	}

	public IUpdater[] getUpdaters() {
		return Arrays.copyOf(updaters, updaters.length);
	}
	
	public long getUpdateTimerPeriodMs() {
		return updateTimerPeriodMs;
	}

	public void setUpdateTimerPeriodMs(long updateTimerPeriodMs) {
		if(updateTimerPeriodMs <= 0) {
			throw new IllegalArgumentException("Update timer period must be greater than 0!");
		}
		
		this.updateTimerPeriodMs = updateTimerPeriodMs;
	}

	public String getExitLine() {
		return exitLine;
	}

	public void setExitLine(String exitLine) {		
		if(exitLine == null) {
			throw new IllegalArgumentException("ExitLine must not be null!");
		}
		
		if(exitLine.isBlank()) {
			throw new IllegalArgumentException("ExitLine must not be blank!");
		}
		
		this.exitLine = exitLine.trim();
	}
}
