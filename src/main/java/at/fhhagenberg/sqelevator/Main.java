package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException, NotBoundException{
		main(System.in, System.out);
	}
	
	public static void main(InputStream input, OutputStream output) throws InterruptedException, IOException, ExecutionException, NotBoundException {
		
		IElevator plc;
		String rootPath = new java.io.File(".").getCanonicalPath() + "\\";
		String appConfigPath = rootPath + "elevator.properties";

		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));
		
		String rmi_address = appProps.getProperty("rmi_address");
		int rmi_port = Integer.parseInt(appProps.getProperty("rmi_port"));
		String mqtt_address = appProps.getProperty("mqtt_address");
		int mqtt_port = Integer.parseInt(appProps.getProperty("mqtt_port"));
		int polling_interval = Integer.parseInt(appProps.getProperty("polling_interval"));
		
		Registry registry = LocateRegistry.getRegistry(rmi_address,rmi_port);
		
		try {
			
			plc = (IElevator) registry.lookup("IElevator");
			
		}catch (Exception e) {
			plc = new ElevatorPlcMock(2, 2, 5);
		}
		
		Building building = new Building(plc);
		ElevatorsMqttClient client = new ElevatorsMqttClient(mqtt_address, mqtt_port);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, client);
		adapter.setUpdateTimerPeriodMs(polling_interval);
		adapter.run(input, output);
	}
}
