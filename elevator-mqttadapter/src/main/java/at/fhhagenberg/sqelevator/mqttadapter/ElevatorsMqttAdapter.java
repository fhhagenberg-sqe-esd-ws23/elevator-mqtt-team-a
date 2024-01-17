package at.fhhagenberg.sqelevator.mqttadapter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.Elevator;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.ExitCommandThread;
import at.fhhagenberg.sqelevator.Floor;

/**
 * This class is the main class of the Elevators MQTT Adapter program.
 * It takes a Building with its elevators and floors and updates them from the Elevators PLC in a configurable interval via the Updater classes.
 * The Bridge classes publish MQTT messages on changes in the Building and listen to MQTT control messages which update the PLC via the Elevator class.
 */
public class ElevatorsMqttAdapter {
	private final Building building;
	private final ElevatorsMqttClient mqtt;
	private final IUpdater[] updaters;
	private final IMqttBridge[] bridges;
	private long updateTimerPeriodMs = 250;

	/**
	 * Create new ElevatorsMqttAdapter for the given building and MQTT client.
	 * @param building the building to use in the adapter
	 * @param mqtt the MQTT client to use in the adapter
	 */
	public ElevatorsMqttAdapter(Building building, ElevatorsMqttClient mqtt) {
		this.building = building;
		this.mqtt = mqtt;

		Elevator[] elevators = building.getElevators();
		Floor[] floors = building.getFloors();
		updaters = new IUpdater[elevators.length + floors.length];
		bridges = new IMqttBridge[elevators.length + floors.length];

		for(int i = 0; i < elevators.length; ++i) {
			updaters[i] = new ElevatorUpdater(elevators[i]);
			bridges[i] = new ElevatorMqttBridge(elevators[i], mqtt);
		}

		for(int i = 0; i < floors.length; ++i) {
			updaters[elevators.length + i] = new FloorUpdater(floors[i]);
			bridges[elevators.length + i] = new FloorMqttBridge(floors[i], mqtt);
		}
	}

	private void startMqttBridges() {
		for(IMqttBridge bridge : bridges) {
			bridge.start();
		}
	}

	private void stopMqttBridges() {
		for(IMqttBridge bridge : bridges) {
			bridge.stop();
		}
	}

	/**
	 * Run the Elevators MQTT Adapter. Periodically poll the RMI interface in a loop until the exit signal.
	 * @param exitThread instance of ExitCommandThread which provides the signal to exit the program
	 * @param output output stream to write information to
	 */
	public void run(ExitCommandThread exitThread, OutputStream output) throws InterruptedException, IOException, ExecutionException {		
		OutputStreamWriter writer = new OutputStreamWriter(output);
		
		if(!mqtt.subscribeToControlMessages(building.getElevatorCount(), building.getFloorCount())) {
			writer.write("Could not subscribe to control messages!\n");
			return;
		}

		mqtt.publishNumberOfElevators(building.getElevatorCount());
		mqtt.publishNumberOfFloors(building.getFloorCount());
		mqtt.publishFloorHeight(building.floorHeight());

		stopMqttBridges();
		startMqttBridges();
		
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

	/**
	 * Provides a copy of the array with the elevator and floor updater objects.
	 * @return an array with the updater objects
	 */
	public IUpdater[] getUpdaters() {
		return Arrays.copyOf(updaters, updaters.length);
	}

	/**
	 * Provides a copy of the array with the elevator and floor MQTT bridge objects.
	 * @return an array with the MQTT bridge objects
	 */
	public IMqttBridge[] getBridges() {
		return Arrays.copyOf(bridges, bridges.length);
	}

	/**
	 * Provides the current RMI polling interval.
	 * @return the current RMI polling interval in ms
	 */
	public long getUpdateTimerPeriodMs() {
		return updateTimerPeriodMs;
	}

	/**
	 * Sets the RMI polling interval.
	 * @param updateTimerPeriodMs the new RMI polling interval in ms
	 */
	public void setUpdateTimerPeriodMs(long updateTimerPeriodMs) {
		if(updateTimerPeriodMs <= 0) {
			throw new IllegalArgumentException("Update timer period must be greater than 0!");
		}
		
		this.updateTimerPeriodMs = updateTimerPeriodMs;
	}
}
