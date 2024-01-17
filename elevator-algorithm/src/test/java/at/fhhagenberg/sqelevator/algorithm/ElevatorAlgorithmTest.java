package at.fhhagenberg.sqelevator.algorithm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import at.fhhagenberg.sqelevator.ElevatorsMqttClient;
import sqelevator.IElevator;

import org.junit.jupiter.api.Test;

public class ElevatorAlgorithmTest {

	
    @Test
    void testAutomaticModeSingleElevatorPressedInsideSequence() throws RemoteException, InterruptedException, ExecutionException {

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
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        elevator0.setStopRequest(2, false);

        elevator0.setStopRequest(2, true);
        elevator0.setFloor(3);
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        elevator0.setStopRequest(3, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(3, true);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        elevator0.setStopRequest(2, false);

        elevator0.setFloor(1);
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
    }
    
    @Test
    void testAutomaticModeSingleElevatorPressedOutsideSequence() throws RemoteException, InterruptedException, ExecutionException {
        
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
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
    	

        floors[1].setButtonUp(true);
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());

        elevator0.setFloor(1);
        floors[2].setButtonDown(false);
        floors[3].setButtonUp(true);
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());

        elevator0.setFloor(3);
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        floors[3].setButtonUp(false);

        elevator0.setFloor(2);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
    }
	
    @Test
    void testAutomaticModeSingleElevatorPressedInsideAndOutsideSequence() throws RemoteException, InterruptedException, ExecutionException {
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
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);

        elevator0.setStopRequest(1, true);
        floors[1].setButtonUp(true);
        
        assertEquals(1, elevator0.getTarget());

        elevator0.setFloor(1);
        floors[2].setButtonDown(true);
        elevator0.setStopRequest(3, true);
        
        assertEquals(2, elevator0.getTarget());
        elevator0.setStopRequest(1, false);

        elevator0.setFloor(3);
        
        assertEquals(2, elevator0.getTarget());
        elevator0.setStopRequest(3, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(1, true);
     
        assertEquals(1, elevator0.getTarget());
        floors[2].setButtonDown(true);
    }
    
    @Test
    void testAutomaticModeSingleElevatorPressedInsideAndOutsideSequenceWithUnservicedFloor() throws InterruptedException, ExecutionException, RemoteException {        
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
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, false);
        

        elevator0.setStopRequest(1, true);
        
        assertEquals(1, elevator0.getTarget());

        elevator0.setFloor(1);
        floors[2].setButtonDown(true);
        elevator0.setStopRequest(3, true);
        
        assertEquals(2, elevator0.getTarget());
        elevator0.setStopRequest(1, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(0, true);
        
        assertEquals(0, elevator0.getTarget());
        floors[2].setButtonDown(true);

        elevator0.setFloor(0);
        floors[3].setButtonDown(true);
        floors[2].setButtonDown(true);
        
        assertEquals(2, elevator0.getTarget());
    }

    @Test
    void testAutomaticModeMultipleElevatorPressedInsideSequence() throws RemoteException, InterruptedException, ExecutionException {
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,4,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, true);
        elevator1.setServicesFloor(3, true);

        elevator0.setStopRequest(1, true);
        elevator0.setStopRequest(3, true);
        elevator1.setStopRequest(2, true);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(2, elevator1.getTarget());

        elevator0.setFloor(1);
        elevator0.setStopRequest(2, true);
        
        assertEquals(3, elevator0.getTarget());
        elevator0.setStopRequest(1, false);

        elevator1.setFloor(2);
        elevator1.setStopRequest(1, true);
        elevator1.setStopRequest(0, true);
        
        assertEquals(1, elevator1.getTarget());
        elevator1.setStopRequest(2, false);

        elevator0.setFloor(2);
        elevator0.setStopRequest(1, true);
        elevator1.setFloor(1);
        
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(0, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator1.getCommittedDirection());
    }

    @Test
    void testAutomaticModeMultipleElevatorPressedOutsideSequence() throws RemoteException, InterruptedException, ExecutionException {
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,5,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
        elevator0.setServicesFloor(4, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, true);
        elevator1.setServicesFloor(3, true);
        elevator0.setServicesFloor(4, true);

        floors[2].setButtonUp(true);
        floors[3].setButtonDown(true);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());

        elevator0.setFloor(2);
        elevator1.setFloor(3);
        floors[4].setButtonUp(true);
        
        assertEquals(4, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator1.getCommittedDirection());
        floors[2].setButtonUp(false);
        floors[3].setButtonDown(false);

        elevator0.setFloor(4);
        floors[1].setButtonDown(true);
        floors[1].setButtonUp(true);
        floors[0].setButtonUp(true);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        assertEquals(1, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator0.getCommittedDirection());
        floors[4].setButtonUp(false);
    }

    @Test
    void testAutomaticModeMultipleElevatorPressedInsideAndOutsideSequence() throws RemoteException, InterruptedException, ExecutionException {
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,5,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
        elevator0.setServicesFloor(4, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, true);
        elevator1.setServicesFloor(3, true);
        elevator0.setServicesFloor(4, true);

        floors[2].setButtonUp(true);
        floors[3].setButtonUp(true);
        elevator0.setStopRequest(1, true);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());

        elevator0.setFloor(1);
        elevator1.setFloor(2);
        floors[4].setButtonUp(true);
        elevator0.setStopRequest(3, true);
        elevator1.setStopRequest(0, true);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());
        elevator0.setStopRequest(1, false);
        floors[2].setButtonUp(false);

        elevator0.setFloor(3);
        elevator1.setFloor(4);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());
    }

    @Test
    void testAutomaticModeMultipleElevatorPressedInsideAndOutsideSequenceWithUnservicedFloor() throws RemoteException, InterruptedException, ExecutionException {        
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,5,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, false);
        elevator0.setServicesFloor(4, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, false);
        elevator1.setServicesFloor(3, true);
        elevator0.setServicesFloor(4, true);

        floors[2].setButtonUp(true);
        floors[3].setButtonUp(true);
        elevator0.setStopRequest(1, true);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(3, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());

        elevator0.setFloor(1);
        elevator1.setFloor(3);
        floors[4].setButtonUp(true);
        elevator1.setStopRequest(0, true);
        
        assertEquals(2, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(0, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator1.getCommittedDirection());
        elevator0.setStopRequest(1, false);
        floors[3].setButtonUp(false);

        elevator0.setFloor(2);
        elevator1.setFloor(4);
        
        assertEquals(4, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(0, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, elevator1.getCommittedDirection());
    }

    @Test
    void testAutomaticModeTwoIdleElevatorsAndFloorButtonPressed() throws RemoteException, InterruptedException, ExecutionException{
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,3,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, true);


        floors[1].setButtonDown(true);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(0, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator1.getCommittedDirection());

        floors[2].setButtonDown(true);
        
        assertEquals(1, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(2, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());
    }

    @Test
    void testAutomaticModeElevatorGoesToTopFloorAndFloorBetweenIsPressed() throws RemoteException, InterruptedException, ExecutionException{
        
    	ElevatorsMqttClient mqttclient = mock(ElevatorsMqttClient.class);
    	when(mqttclient.isConnected()).thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any()))
               .thenReturn(true);
        when(mqttclient.subscribe_int(anyString(),any(),any(),any()))
        .thenReturn(true);
        AlgorithmMqttAdapter ama = new AlgorithmMqttAdapter(mqttclient,2,4,10);
        ElevatorAlgorithm logic = new ElevatorAlgorithm(ama);
        var building = ama.getBuilding();
        var elevator0 = building.getElevators()[0];
        var elevator1 = building.getElevators()[1];
        var floors = building.getFloors();
        elevator0.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator0.setServicesFloor(0, true);
        elevator0.setServicesFloor(1, true);
        elevator0.setServicesFloor(2, true);
        elevator0.setServicesFloor(3, true);
        elevator1.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        elevator1.setServicesFloor(0, true);
        elevator1.setServicesFloor(1, true);
        elevator1.setServicesFloor(2, true);
        elevator1.setServicesFloor(3, true);

        elevator0.setStopRequest(3, true);
        
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(0, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator1.getCommittedDirection());

        elevator0.setFloor(1);
        floors[2].setButtonUp(true);
        
        assertEquals(3, elevator0.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator0.getCommittedDirection());
        assertEquals(2, elevator1.getTarget());
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator1.getCommittedDirection());
    }
	
}
