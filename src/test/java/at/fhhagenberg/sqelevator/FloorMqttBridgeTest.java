package at.fhhagenberg.sqelevator;

import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FloorMqttBridgeTest {
	private ElevatorsMqttClient mqtt;
	private Floor floor;
	private FloorMqttBridge bridge;
	
	@BeforeEach
	void setup() {
		mqtt = mock(ElevatorsMqttClient.class);
		floor = mock(Floor.class);
		
		bridge = new FloorMqttBridge(floor, mqtt);
	}
	
	@Test
	void testStart() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(true);
		when(floor.getNumber()).thenReturn(0);
		
		bridge.start();
		
		verify(mqtt, times(1)).publishButtonUp(0, false);
		verify(mqtt, times(1)).publishButtonDown(0, true);
		verify(floor, times(1)).addPropertyChangeListener(bridge);
	}
	
	@Test
	void testStartTwice() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(false);
		when(floor.getNumber()).thenReturn(0);
		
		bridge.start();
		bridge.start();
		
		verify(mqtt, times(1)).publishButtonUp(0, false);
		verify(mqtt, times(1)).publishButtonDown(0, false);
		verify(floor, times(1)).addPropertyChangeListener(bridge);
	}
	
	@Test
	void testStop() {
		bridge.stop();
		
		verify(floor, times(1)).removePropertyChangeListener(bridge);
	}
	
	@Test
	void testPropertyChange_ButtonUp() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(false);
		when(floor.getNumber()).thenReturn(0);
		
		boolean oldValue = false;
		boolean newValue = true;
		PropertyChangeEvent event = new PropertyChangeEvent(floor, Floor.BUTTON_UP_PROPERTY_NAME, oldValue, newValue);
		
		bridge.start();
		bridge.propertyChange(event);
		
		verify(mqtt, times(2)).publishButtonUp(0, false);
		verify(mqtt, times(1)).publishButtonDown(0, false);
	}
	
	@Test
	void testPropertyChange_ButtonDown() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(false);
		when(floor.getNumber()).thenReturn(0);
		
		boolean oldValue = false;
		boolean newValue = true;
		PropertyChangeEvent event = new PropertyChangeEvent(floor, Floor.BUTTON_DOWN_PROPERTY_NAME, oldValue, newValue);
		
		bridge.start();
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishButtonUp(0, false);
		verify(mqtt, times(2)).publishButtonDown(0, false);
	}
	
	@Test
	void testPropertyChange_InvalidPropertySource() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(false);
		when(floor.getNumber()).thenReturn(0);
		
		boolean oldValue = false;
		boolean newValue = true;
		Object invalid = new Object();
		PropertyChangeEvent event = new PropertyChangeEvent(invalid, Floor.BUTTON_DOWN_PROPERTY_NAME, oldValue, newValue);
		
		bridge.start();
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishButtonUp(0, false);
		verify(mqtt, times(1)).publishButtonDown(0, false);
	}

	@Test
	void testPropertyChange_InvalidPropertyName() {
		when(floor.isButtonUp()).thenReturn(false);
		when(floor.isButtonDown()).thenReturn(false);
		when(floor.getNumber()).thenReturn(0);
		
		boolean oldValue = false;
		boolean newValue = true;
		PropertyChangeEvent event = new PropertyChangeEvent(floor, "invalid", oldValue, newValue);
		
		bridge.start();
		bridge.propertyChange(event);
		
		verify(mqtt, times(1)).publishButtonUp(0, false);
		verify(mqtt, times(1)).publishButtonDown(0, false);
	}
}
