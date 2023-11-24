package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException{
		main(System.in, System.out);
	}
	
	public static void main(InputStream input, OutputStream output) throws InterruptedException, IOException, ExecutionException {
		IElevator plc = new ElevatorPlcMock(2, 2, 5);
		Building building = new Building(plc);
		ElevatorsMqttClient client = new ElevatorsMqttClient("localhost", 6789);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, client);
		adapter.run(input, output);
	}
}
