package at.fhhagenberg.sqelevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import org.mockito.junit.jupiter.MockitoExtension;

import sqelevator.IElevator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class FloorTest {
	
	@Test
	void testFloorButtonDownPressed_FloorButtonUpNotPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorButtonDown(0)).thenReturn(true);
		when(plc.getFloorButtonUp(0)).thenReturn(false);
		
		Floor floorObj = new Floor(plc,0);
		
		assertTrue(floorObj.isButtonDown());
		assertFalse(floorObj.isButtonUp());
	}

	@Test
	void testFloorButtonDownNotPressed_FloorButtonUpPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorButtonDown(0)).thenReturn(false);
		when(plc.getFloorButtonUp(0)).thenReturn(true);
		
		Floor floorObj = new Floor(plc,0);
		
		assertFalse(floorObj.isButtonDown());
		assertTrue(floorObj.isButtonUp());
	}
	
	@Test
	void testFloorButtonUpAndDownPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorButtonDown(0)).thenReturn(true);
		when(plc.getFloorButtonUp(0)).thenReturn(true);
		
		Floor floorObj = new Floor(plc,0);
		
		assertTrue(floorObj.isButtonDown());
		assertTrue(floorObj.isButtonUp());
	}
	
	@Test
	void testFloorButtonUpAndDownNOTPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		when(plc.getFloorButtonDown(0)).thenReturn(false);
		when(plc.getFloorButtonUp(0)).thenReturn(false);
		
		Floor floorObj = new Floor(plc,0);
		
		assertFalse(floorObj.isButtonDown());
		assertFalse(floorObj.isButtonUp());
	}
	
	@Test
	void testFloorGetNumber_Zero() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		
		assertEquals(0,floorObj.getNumber());
	}
	
	@Test
	void testFloorGetNumber_Negative() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,-1);
		
		assertEquals(-1,floorObj.getNumber());
	}
	
	@Test
	void testFloorGetNumber_HighPositive() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,99);
		
		assertEquals(99,floorObj.getNumber());
	}
	
	@Test
	void testFloorSetButtonDownPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		floorObj.setButtonDown(true);
		
		assertTrue(floorObj.isButtonDown());
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_DOWN_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == true));
	}
	
	@Test
	void testFloorSetButtonUpPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		floorObj.setButtonUp(true);
		
		assertTrue(floorObj.isButtonUp());
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == true));
	}
	
	@Test
	void testFloorSetButtonDownNotPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		floorObj.setButtonDown(false);
		
		assertFalse(floorObj.isButtonDown());
		verify(listener, times(0)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_DOWN_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == false));
	}
	
	@Test
	void testFloorSetButtonUpNotPressed() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		
		floorObj.setButtonUp(false);
		assertFalse(floorObj.isButtonUp());
		verify(listener, times(0)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == false));
		
	}
	
	@Test
	void testFloorSetButtonUpPress_thenResetPress() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		
		floorObj.setButtonUp(true);
		assertTrue(floorObj.isButtonUp());
		floorObj.setButtonUp(false);
		assertFalse(floorObj.isButtonUp());
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == true && (boolean)event.getNewValue() == false));
		
	}
	
	@Test
	void testFloorSetButtonDownPress_thenResetPress() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		floorObj.setButtonDown(true);
		assertTrue(floorObj.isButtonDown());
		floorObj.setButtonDown(false);
		assertFalse(floorObj.isButtonDown());
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_DOWN_PROPERTY_NAME && (boolean)event.getOldValue() == true && (boolean)event.getNewValue() == false));
	}
	
	@Test
	void testFloorGetPlc() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		
		assertEquals(plc,floorObj.getPlc());
	}
	
	@Test
	void testFloorCreateWithNullPlc() throws RemoteException
	{
		IElevator plc = null;
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()->new Floor(plc,0));		
		assertEquals("Plc must be valid!", thrown.getMessage());
	}
	
	@Test
	void testFloorRemovePropertyChangeListener() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		
		
		floorObj.setButtonUp(true);
		floorObj.removePropertyChangeListener(listener);
		floorObj.setButtonUp(false);

		
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == true));
	}
	
	@Test
	void testFloorAddPropertyChangeListenerTwoTimes_thenRemoveOneAfterFirstEvent() throws RemoteException
	{
		IElevator plc = mock(IElevator.class);
		Floor floorObj = new Floor(plc,0);
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		floorObj.addPropertyChangeListener(listener);
		floorObj.addPropertyChangeListener(listener);
		
		floorObj.setButtonUp(true);
		verify(listener, times(2)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == false && (boolean)event.getNewValue() == true));
		
		floorObj.removePropertyChangeListener(listener);
		floorObj.setButtonUp(false);
		verify(listener, times(1)).propertyChange(argThat(event -> event.getPropertyName() == Floor.BUTTON_UP_PROPERTY_NAME && (boolean)event.getOldValue() == true && (boolean)event.getNewValue() == false));
	}
}
