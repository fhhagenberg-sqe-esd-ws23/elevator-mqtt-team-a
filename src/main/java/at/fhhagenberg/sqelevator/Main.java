package at.fhhagenberg.sqelevator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
		main(args, System.in, System.out);
	}
	
	public static void main(String[] args, InputStream input, OutputStream output) throws InterruptedException, IOException, ExecutionException {
		ElevatorProperties props = new ElevatorProperties();
		
		ElevatorsMqttClient mqtt = new ElevatorsMqttClient(props.getMqttAddress(), props.getMqttPort());
		mqtt.connect();
		
		ExitCommandThread exitThread = new ExitCommandThread(input, props.getExitLine());
		exitThread.start();
		
		OutputStreamWriter writer = new OutputStreamWriter(output);
		writer.write("Enter \"" + props.getExitLine() + "\" to stop the application.\n");
		writer.flush();
		
		if(args.length > 0 && args[0] != null && args[0].contains("rmimock")) {					
			IElevator plc = new ElevatorPlcMock(2, 2, 5);
			run(plc, mqtt, exitThread, output, props.getRmiPollingInterval());
		}
		else {
			ElevatorsPlcConnection plc = new ElevatorsPlcConnection(props);
			
			while(!exitThread.isExitRequest()) {				
				try {
					if(plc.connect(output)) {
						run(plc, mqtt, exitThread, output, props.getRmiPollingInterval());
					}
				}
				catch(RemoteException e) {
					try {
						writer.write("Lost connection to RMI API: ");
						writer.write(e.getMessage());
						writer.write("\nTry to reconnect ...\n");
						writer.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				if(exitThread.isExitRequest()) {
					break;
				}

				Thread.sleep(2500);
			}
		}
		
		writer.write("Exited on user request.\n");
		writer.flush();

		mqtt.disconnect();
		exitThread.join();
	}
	
	private static void run(IElevator plc, ElevatorsMqttClient mqtt, ExitCommandThread exitThread, OutputStream output, int pollingInterval) throws InterruptedException, IOException, ExecutionException {
		Building building = new Building(plc);
		ElevatorsMqttAdapter adapter = new ElevatorsMqttAdapter(building, mqtt);
		adapter.setUpdateTimerPeriodMs(pollingInterval);
		adapter.run(exitThread, output);
	}
}
