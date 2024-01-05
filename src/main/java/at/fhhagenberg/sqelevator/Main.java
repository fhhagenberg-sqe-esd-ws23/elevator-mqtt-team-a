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
		
		while(!exitThread.isExitRequest()) {
			IElevator plc;
			
			try {
				if(args.length > 0 && args[0] != null && args[0].contains("rmimock")) {					
					plc = new ElevatorPlcMock(2, 2, 5);		
				}
				else {
					plc = connect(props.getRmiAddress(), props.getRmiPort(), output);
				}

				if(plc != null) {
					run(plc, mqtt, exitThread, output, props.getRmiPollingInterval());
				}
			}
			catch(RemoteException e) {
				try {
					writer.write("Lost connection to RMI API: ");
					writer.write(e.getMessage());
					writer.write(" |=> Try to reconnect ...\n");
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
	
	private static IElevator connect(String rmi_address, int rmi_port, OutputStream output) {
		try {
			Registry registry = LocateRegistry.getRegistry(rmi_address, rmi_port);
			
			try {
				return (IElevator) registry.lookup("IElevator");
			}
			catch(NotBoundException e) {
				try {
					OutputStreamWriter writer = new OutputStreamWriter(output);
					writer.write("Name not found during registry lookup: ");
					writer.write(e.getMessage());
					writer.write(" |=> Retrying ...\n");
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
					writer.write(" |=> Retrying ...\n");
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
					writer.write(" |=> Retrying ...\n");
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
				writer.write(" |=> Retrying ...\n");
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return null;
	}
}
