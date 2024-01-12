package at.fhhagenberg.sqelevator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class reads the required configuration file elevator.properties and provides getters to its contents.
 * The file is read and parsed (only) in the constructor and there is no internal error handling.
 */
public class ElevatorProperties {
	private final String rmiAddress;
	private final int rmiPort;
	private final String rmiName;
	private final String mqttAddress;
	private final int mqttPort;
	private final int rmiPollingInterval;
	private final String exitLine;

	/**
	 * Create new ElevatorProperties object and try to parse the elevator.properties file.
	 * @throws IOException
	 */
	public ElevatorProperties() throws IOException {
		String rootPath = new File(".").getCanonicalPath() + "\\";
		String appConfigPath = rootPath + "elevator.properties";

		Properties appProps = new Properties();
		
		try(FileInputStream stream = new FileInputStream(appConfigPath)) {
			appProps.load(stream);

			rmiAddress = appProps.getProperty("rmi_address");
			rmiPort = Integer.parseInt(appProps.getProperty("rmi_port"));
			rmiName = appProps.getProperty("rmi_name");
			mqttAddress = appProps.getProperty("mqtt_address");
			mqttPort = Integer.parseInt(appProps.getProperty("mqtt_port"));
			rmiPollingInterval = Integer.parseInt(appProps.getProperty("polling_interval"));
			exitLine = appProps.getProperty("exit_line");			
		}
	}

	public String getRmiAddress() {
		return rmiAddress;
	}

	public int getRmiPort() {
		return rmiPort;
	}
	
	public String getRmiName() {
		return rmiName;
	}

	public String getMqttAddress() {
		return mqttAddress;
	}

	public int getMqttPort() {
		return mqttPort;
	}

	public int getRmiPollingInterval() {
		return rmiPollingInterval;
	}

	public String getExitLine() {
		return exitLine;
	}
}
