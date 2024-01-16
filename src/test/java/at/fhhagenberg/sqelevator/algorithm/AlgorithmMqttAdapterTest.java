package at.fhhagenberg.sqelevator.algorithm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import at.fhhagenberg.sqelevator.MqttTopicGenerator;
import sqelevator.IElevator;

@ExtendWith(MockitoExtension.class)
public class AlgorithmMqttAdapterTest {
	private final MqttTopicGenerator topics = new MqttTopicGenerator();

	@Container
	HiveMQContainer container = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));

	private Mqtt3BlockingClient testClient;
	private ElevatorsMqttClient mqtt;

	@BeforeEach
	void setUp() throws InterruptedException, ExecutionException {
		container.start();

		testClient = Mqtt3Client.builder().serverPort(container.getMqttPort()).buildBlocking();
		testClient.connect();

		mqtt = new ElevatorsMqttClient(container.getHost(), container.getMqttPort());
		mqtt.connect();
	}

	@Test
	void testAlgorithmMqttAdapter() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
	}

	@Test
	void testAlgoritmMqttAdapterThrowsRuntimeException() throws InterruptedException, ExecutionException {
		mqtt.disconnect();
		RemoteException thrown = assertThrows(RemoteException.class, () -> new AlgorithmMqttAdapter(mqtt, 1, 2, 2));
		assertEquals("could not subscribe to all status messages", thrown.getMessage());
	}

	@Test
	void testGetCommitedDirection() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, adapter.getCommittedDirection(0));
	}

	@Test
	void testGetCommitedDirection_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getCommittedDirection(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetCommitedDirection_ElevatorNumberToLow()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getCommittedDirection(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorAccel() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorAccel(0));
	}

	@Test
	void testGetElevatorAccel_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorAccel(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorAccel_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorAccel(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorButton() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertFalse(adapter.getElevatorButton(0, 1));
	}

	@Test
	void testGetElevatorButton_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorButton(1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorButton_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorButton(-1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorButton_FloorToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorButton(0, 2));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetElevatorButton_FloorToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorButton(0, -1));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetElevatorDoorStatus() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(IElevator.ELEVATOR_DOORS_CLOSED, adapter.getElevatorDoorStatus(0));
	}

	@Test
	void testGetElevatorDoorStatus_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorDoorStatus(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorDoorStatus_ElevatorNumberToLow()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorDoorStatus(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorFloor() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorFloor(0));
	}

	@Test
	void testGetElevatorFloor_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorFloor(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorFloor_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorFloor(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorNum() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(1, adapter.getElevatorNum());
	}

	@Test
	void testGetElevatorPosition() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorPosition(0));
	}

	@Test
	void testGetElevatorPosition_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorPosition(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorPosition_ElevatorNumberToLow()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorPosition(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorSpeed() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorSpeed(0));
	}

	@Test
	void testGetElevatorSpeed_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorSpeed(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorSpeed_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorSpeed(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorWeight() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorWeight(0));
	}

	@Test
	void testGetElevatorWeight_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorWeight(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorWeight_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorWeight(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorCapacity() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getElevatorCapacity(0));
	}

	@Test
	void testGetElevatorCapacity_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorCapacity(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetElevatorCapacity_ElevatorNumberToLow()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getElevatorCapacity(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetFloorButtonDown() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertFalse(adapter.getFloorButtonDown(0));
	}

	@Test
	void testGetFloorButtonDown_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getFloorButtonDown(2));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetFloorButtonDown_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getFloorButtonDown(-1));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetFloorButtonUp() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertFalse(adapter.getFloorButtonUp(0));
	}

	@Test
	void testGetFloorButtonUp_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getFloorButtonUp(2));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetFloorButtonUp_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getFloorButtonUp(-1));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetFloorHeight() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(2, adapter.getFloorHeight());
	}

	@Test
	void testGetFloorNum() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(2, adapter.getFloorNum());
	}

	@Test
	void testGetServicesFloors() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertFalse(adapter.getServicesFloors(0, 1));
	}

	@Test
	void testGetServicesFloors_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getServicesFloors(1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetServicesFloors_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getServicesFloors(-1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetServicesFloors_FloorToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getServicesFloors(0, 2));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetServicesFloors_FloorToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.getServicesFloors(0, -1));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testGetTarget() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getTarget(0));
	}

	@Test
	void testGetTarget_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.getTarget(1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetTarget_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.getTarget(-1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testGetClockTick() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		assertEquals(0, adapter.getClockTick());
	}

	@Test
	void testSetCommitedDirection() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		adapter.setCommittedDirection(0, IElevator.ELEVATOR_DIRECTION_UP);

		assertEquals(IElevator.ELEVATOR_DIRECTION_UP, adapter.getCommittedDirection(0));
	}

	@Test
	void testSetCommitedDirection_InvalidDirection() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setCommittedDirection(0, 3));
		assertEquals("Invalid direction!", thrown.getMessage());
	}

	@Test
	void testSetCommitedDirection_ElevatorNumberToHigh()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setCommittedDirection(1, IElevator.ELEVATOR_DIRECTION_DOWN));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testSetCommitedDirection_ElevatorNumberToLow()
			throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setCommittedDirection(-1, IElevator.ELEVATOR_DIRECTION_DOWN));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testSetServicesFloors() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		adapter.setServicesFloors(0, 0, true);

		assertTrue(adapter.getServicesFloors(0, 0));
	}

	@Test
	void testSetServicesFloors_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setServicesFloors(1, 0, true));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testSetServicesFloors_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setServicesFloors(-1, 0, true));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testSetServicesFloors_FloorToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setServicesFloors(0, 2, true));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testSetServicesFloors_FloorToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> adapter.setServicesFloors(0, -1, true));
		assertEquals("Invalid floor!", thrown.getMessage());
	}

	@Test
	void testSetTarget() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		adapter.setTarget(0, 1);

		assertEquals(1, adapter.getTarget(0));
	}

	@Test
	void testSetTarget_TargetToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.setTarget(0, 2));
		assertEquals("Invalid target floor!", thrown.getMessage());
	}

	@Test
	void testSetTarget_TargetToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.setTarget(0, -1));
		assertEquals("Invalid target floor!", thrown.getMessage());
	}

	@Test
	void testSetTarget_ElevatorNumberToHigh() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.setTarget(1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@Test
	void testSetTarget_ElevatorNumberToLow() throws RemoteException, InterruptedException, ExecutionException {
		AlgorithmMqttAdapter adapter = new AlgorithmMqttAdapter(mqtt, 1, 2, 2);
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.setTarget(-1, 1));
		assertEquals("Invalid elevator number!", thrown.getMessage());
	}

	@AfterEach
	void cleanUp() throws InterruptedException, ExecutionException {
		mqtt.disconnect();
		testClient.disconnect();
		container.stop();
	}
}
