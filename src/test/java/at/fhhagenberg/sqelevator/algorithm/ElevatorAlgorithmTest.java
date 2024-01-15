package at.fhhagenberg.sqelevator.algorithm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import org.mockito.junit.jupiter.MockitoExtension;

import at.fhhagenberg.sqelevator.Building;
import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import sqelevator.IElevator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class ElevatorAlgorithmTest {

	
    @Test
    void testAutomaticModeSingleElevatorPressedInsideSequence() throws RemoteException, InterruptedException, ExecutionException {
    	
		/*
		 * IElevator plc = mock(IElevator.class);
		 * when(plc.getElevatorNum()).thenReturn(1);
		 * when(plc.getFloorNum()).thenReturn(4);
		 * when(plc.getFloorHeight()).thenReturn(10);
		 * when(plc.getElevatorDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED
		 * ); AlgorithmMqttAdapter adapterMock = mock(AlgorithmMqttAdapter.class);
		 * Building building = new Building(plc);
		 * when(adapterMock.getBuilding()).thenReturn(building);
		 */
    	
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
        
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,1,4,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);

        
        elevator0.setStopRequest(1, true);
        elevator0.setStopRequest(3, true);
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());

        elevator0.setStopRequest(2, true);
        elevator0.setFloor(1);
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        elevator0.setStopRequest(1, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(1, true);
        logic.setNextTargets();
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        elevator0.setStopRequest(2, false);

        elevator0.setStopRequest(2, true);
        elevator0.setFloor(3);
        logic.setNextTargets();
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        elevator0.setStopRequest(3, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(3, true);
        logic.setNextTargets();
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        elevator0.setStopRequest(2, false);

        elevator0.setFloor(1);
        logic.setNextTargets();
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
    }
    
    @Test
    void testAutomaticModeSingleElevatorPressedOutsideSequence() {
        
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,1,4,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
    	
    	ModelFactory factory = new ModelFactory(new MockElevatorService(1, 4, 10));
        var building = factory.createBuilding();
        BusinessLogic logic = new BusinessLogic(building);
        var elevator0 = building.getElevatorByNumber(0);
        elevator0.setDoorStatus(IElevatorService.ELEVATOR_DOORS_OPEN);
        var floors = building.getFloors();

        floors.get(1).setWantUp(true);
        logic.setNextTargets();
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevatorService.ELEVATOR_DIRECTION_UP, elevator0.getDirection());

        elevator0.setFloor(1);
        floors.get(2).setWantDown(true);
        floors.get(3).setWantUp(true);
        logic.setNextTargets();
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevatorService.ELEVATOR_DIRECTION_UP, elevator0.getDirection());

        elevator0.setFloor(3);
        logic.setNextTargets();
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevatorService.ELEVATOR_DIRECTION_DOWN, elevator0.getDirection());
        floors.get(3).setWantUp(false);

        elevator0.setFloor(2);
        logic.setNextTargets();
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevatorService.ELEVATOR_DIRECTION_DOWN, elevator0.getDirection());
    }
	
	
	
}
