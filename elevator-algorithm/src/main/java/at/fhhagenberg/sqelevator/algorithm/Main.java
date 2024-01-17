package at.fhhagenberg.sqelevator.algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

import at.fhhagenberg.sqelevator.ElevatorProperties;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.ExitCommandThread;
import at.fhhagenberg.sqelevator.MqttTopicGenerator;

public class Main {

	private final MqttTopicGenerator topics = new MqttTopicGenerator();
	private int numElevators = -1;
	private int numFloors = -1;
	private int floorHeight = -1;

	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
		Main main = new Main();
		ElevatorProperties props = new ElevatorProperties();
		main.run(args, props, System.in, System.out);
	}

	public void run(String[] args, ElevatorProperties props, InputStream in, OutputStream out) throws IOException, InterruptedException, ExecutionException {
		ElevatorsMqttClient mqtt = new ElevatorsMqttClient(props.getMqttAddress(), props.getMqttPort());

		ExitCommandThread exitThread = new ExitCommandThread(in, props.getExitLine());
		exitThread.start();

		OutputStreamWriter writer = new OutputStreamWriter(out);
		writer.write("Enter \"" + props.getExitLine() + "\" to stop the application.\n");
		writer.flush();

		writer.write("Waiting for initial mqtt messages to arrive.\n");
		writer.flush();

		mqtt.connect();

		if (!mqtt.subscribe_int(topics.getNumElevatorsTopic(),
				(args0, intval)->{
					numElevators = (int)intval;})) {
			writer.write("num elevator topic subscription failed");
			return;
		}

		if (!mqtt.subscribe_int(topics.getNumFloorsTopic(),
				(args0, intval)->{
					numFloors = (int)intval;})) {
			writer.write("num Floors topic subscription failed");
			return;
		}

		if (!mqtt.subscribe_int(topics.getFloorHeightTopic(),
				(args0, intval)->{
					floorHeight = (int)intval;})) {
			writer.write("floor height topic subscription failed");
			return;
		}

		while(numElevators == -1 || numFloors == -1 || floorHeight == -1){
			Thread.sleep(100);

			if(exitThread.isExitRequest()) {
				writer.write("Exited on user request.\n");
				writer.flush();
				return;
			}
		}

		writer.write("got initial data...");
		writer.flush();

		AlgorithmMqttAdapter mqttAdapter = new AlgorithmMqttAdapter(mqtt,numElevators, numFloors, floorHeight);
		ElevatorAlgorithm elevatorAlgorithm = new ElevatorAlgorithm(mqttAdapter);

		elevatorAlgorithm.setWaitForInitialStatusReceived(true);
		
		if(!mqttAdapter.subscribeToStatusMessages()) {
			writer.write("could not subscribe to all status messages\n");
			writer.flush();
			return;
		}

		while(!exitThread.isExitRequest()) {
			Thread.sleep(2500);
		}

		elevatorAlgorithm.Shutdown();	
	}
}
