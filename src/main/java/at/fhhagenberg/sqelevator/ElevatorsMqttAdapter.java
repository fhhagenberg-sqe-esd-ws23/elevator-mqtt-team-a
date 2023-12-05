package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class ElevatorsMqttAdapter {
	private final IUpdater[] updaters;
	private final ElevatorsMqttClient mqtt;
	private final ElevatorMqttBridge[] elevatorBridges;
	private final FloorMqttBridge[] floorBridges;
	private long updateTimerPeriodMs = 250;
	private String exitLine = "exit";
	private volatile boolean external_shutdown = false;

	public ElevatorsMqttAdapter(Building building, ElevatorsMqttClient mqtt) throws RemoteException {
		Elevator[] elevators = building.getElevators();
		Floor[] floors = building.getFloors();
		updaters = new IUpdater[elevators.length + floors.length];
		elevatorBridges = new ElevatorMqttBridge[elevators.length];
		floorBridges = new FloorMqttBridge[floors.length];
		this.mqtt = mqtt;
		
		for(int i = 0; i < elevators.length; ++i) {
			updaters[i] = new ElevatorUpdater(elevators[i]);
			elevatorBridges[i] = new ElevatorMqttBridge(elevators[i], mqtt);
		}
		
		for(int i = 0; i < floors.length; ++i) {
			updaters[elevators.length + i] = new FloorUpdater(floors[i]);
			floorBridges[i] = new FloorMqttBridge(floors[i], mqtt);
		}
	}
	
	private void startMqttBridges() {
		for(ElevatorMqttBridge bridge : elevatorBridges) {
			bridge.start();
		}
		
		for(FloorMqttBridge bridge : floorBridges) {
			bridge.start();
		}
	}
	
	private void stopMqttBridges() {
		for(ElevatorMqttBridge bridge : elevatorBridges) {
			bridge.stop();
		}
		
		for(FloorMqttBridge bridge : floorBridges) {
			bridge.stop();
		}
	}
	
	public void run(InputStream input, OutputStream output) throws InterruptedException, IOException, ExecutionException {		
		InputStreamThread thread = new InputStreamThread(input, exitLine);
		thread.start();
		mqtt.subscribeToControlMessages(elevatorBridges.length, floorBridges.length);
		mqtt.connect();
		startMqttBridges();
		
		OutputStreamWriter writer = new OutputStreamWriter(output);
		writer.write("Started Elevators Mqtt Adapter.\n");
		writer.write("Enter \"" + exitLine + "\" to stop the application.\n");
		writer.flush();
				
		while(true) {
			Thread.sleep(updateTimerPeriodMs);
			
			if(thread.isExitRequest() || external_shutdown) {
				thread.join();
				writer.write("Exited on user request.\n");
				writer.flush();
				break;
			}
			
			for(IUpdater updater : updaters) {
				updater.update();
			}
		}
		
		stopMqttBridges();
		mqtt.disconnect();
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
