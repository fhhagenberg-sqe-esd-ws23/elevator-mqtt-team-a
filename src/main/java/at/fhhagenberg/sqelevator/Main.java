package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException{
		main(System.in, System.out);
	}
	
	public static void main(InputStream input, OutputStream output) throws InterruptedException, IOException {
		IElevator plc = new ElevatorPlcMock(2, 2, 5);
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building);
		adapter.run(input, output);
	}
}
