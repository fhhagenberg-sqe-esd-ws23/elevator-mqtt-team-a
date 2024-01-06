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
	
	public void run(ExitCommandThread exitThread, OutputStream output) throws InterruptedException, IOException, ExecutionException {
		mqtt.unsubscribeAll();
		mqtt.subscribeToControlMessages(elevatorBridges.length, floorBridges.length);
		startMqttBridges();
		
		OutputStreamWriter writer = new OutputStreamWriter(output);
		writer.write("Started Elevators Mqtt Adapter.\n");
				
		while(!exitThread.isExitRequest()) {
			Thread.sleep(updateTimerPeriodMs);
			
			if(exitThread.isExitRequest()) {
				exitThread.join();
				break;
			}
			
			for(IUpdater updater : updaters) {
				updater.update();
			}
		}
		
		stopMqttBridges();
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
}
