package at.fhhagenberg.sqelevator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ElevatorProperties {
	private final String rmiAddress;
	private final int rmiPort;
	private final String mqttAddress;
	private final int mqttPort;
	private final int rmiPollingInterval;
	private final String exitLine;

	public ElevatorProperties() throws IOException {
		String rootPath = new File(".").getCanonicalPath() + "\\";
		String appConfigPath = rootPath + "elevator.properties";

		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));

		rmiAddress = appProps.getProperty("rmi_address");
		rmiPort = Integer.parseInt(appProps.getProperty("rmi_port"));
		mqttAddress = appProps.getProperty("mqtt_address");
		mqttPort = Integer.parseInt(appProps.getProperty("mqtt_port"));
		rmiPollingInterval = Integer.parseInt(appProps.getProperty("polling_interval"));
		exitLine = appProps.getProperty("exit_line");
	}

	public String getRmiAddress() {
		return rmiAddress;
	}

	public int getRmiPort() {
		return rmiPort;
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
