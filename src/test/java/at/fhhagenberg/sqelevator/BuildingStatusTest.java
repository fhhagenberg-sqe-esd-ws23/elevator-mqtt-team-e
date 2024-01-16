package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class BuildingStatusTest {

    @Mock
    private MqttWrapper mockMqttClient;
    @Mock
    private IElevator mockElController;

    private BuildingStatus building = null;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor(){
        building = new BuildingStatus(mockMqttClient, "");
        assertNotNull(building);

        assertEquals(mockMqttClient, building.getMqttClient());
        assertEquals("", building.getRmiConnectionString());

        BuildingStatus building2 = new BuildingStatus(mockMqttClient, "test");
        assertNotNull(building2);
        assertEquals("test", building2.getRmiConnectionString());
    }

    @Test
    void testInit() throws RemoteException {
        int elevNum = 2;
        int floorNum = 2;
        building = new BuildingStatus(mockMqttClient, "");
        assertNotNull(building);

        building.setElevatorController(mockElController);
        assertEquals(mockElController, building.getElevatorController());

        when(mockElController.getFloorNum()).thenReturn(floorNum);
        when(mockElController.getElevatorNum()).thenReturn(elevNum);

        when(mockElController.getFloorButtonUp(anyInt())).thenReturn(true, false);
        when(mockElController.getFloorButtonDown(anyInt())).thenReturn(false, true);

        building.init();

        var elevators = building.getElevators();
        var buttonsUp = building.getButtonPressedUp();
        var buttonsDown = building.getButtonPressedDown();

        assertNotNull(elevators);
        assertEquals(elevNum, elevators.length);
        assertEquals(floorNum, buttonsUp.length);
        assertEquals(floorNum, buttonsDown.length);

        assertEquals(true, buttonsUp[0]);
        assertEquals(false, buttonsUp[1]);
        assertEquals(false, buttonsDown[0]);
        assertEquals(true, buttonsDown[1]);

        verify(mockMqttClient).publishRetainedMQTTMessage("NumberElevators/", Integer.toString(elevNum));
        verify(mockMqttClient).publishRetainedMQTTMessage("NumberFloors/", Integer.toString(floorNum));
    }

    @Test
    void testReportTargetToRMI() throws RemoteException {
        building = new BuildingStatus(mockMqttClient, "");
        building.setElevatorController(mockElController);

        building.reportTargetToRMI(1,2);
        verify(mockElController).setTarget(1,2);
    }

    @Test
    void testReportCommitedDirectionToRMI() throws RemoteException {
        building = new BuildingStatus(mockMqttClient, "");
        building.setElevatorController(mockElController);

        building.reportCommitedDirectionToRMI(1,0);
        verify(mockElController).setCommittedDirection(1,0);
    }

    @Test
    void testSendStatus() throws RemoteException {
        int elevNum = 2;
        int floorNum = 2;
        building = new BuildingStatus(mockMqttClient, "");
        assertNotNull(building);

        building.setElevatorController(mockElController);
        assertEquals(mockElController, building.getElevatorController());

        when(mockElController.getFloorNum()).thenReturn(floorNum);
        when(mockElController.getElevatorNum()).thenReturn(elevNum);

        when(mockElController.getFloorButtonUp(anyInt())).thenReturn(true, false, false, false);
        when(mockElController.getFloorButtonDown(anyInt())).thenReturn(false, true, true, true);

        building.init();

        ElevatorStatus[] elevators = new ElevatorStatus[elevNum];
        elevators[0] = mock(ElevatorStatus.class);
        elevators[1] = mock(ElevatorStatus.class);
        building.setElevators(elevators);

        building.sendStatus();

        var buttonsUp = building.getButtonPressedUp();
        var buttonsDown = building.getButtonPressedDown();

        assertEquals(false, buttonsUp[0]);
        assertEquals(false, buttonsUp[1]);
        assertEquals(true, buttonsDown[0]);
        assertEquals(true, buttonsDown[1]);

        verify(mockMqttClient).publishMQTTMessage("FloorButtonUp/" + 0 + "/", Boolean.toString(false));
        verify(mockMqttClient).publishMQTTMessage("FloorButtonUp/" + 1 + "/", Boolean.toString(false));
        verify(mockMqttClient).publishMQTTMessage("FloorButtonDown/" + 0 + "/", Boolean.toString(true));
        verify(mockMqttClient).publishMQTTMessage("FloorButtonDown/" + 1 + "/", Boolean.toString(true));

        verify(elevators[0]).checkStatus(false);
        verify(elevators[1]).checkStatus(false);
    }

    @Test
    void testSendStatusMutation() throws RemoteException {
        int elevNum = 2;
        int floorNum = 2;
        building = new BuildingStatus(mockMqttClient, "");
        assertNotNull(building);

        building.setElevatorController(mockElController);
        assertEquals(mockElController, building.getElevatorController());

        when(mockElController.getFloorNum()).thenReturn(floorNum);
        when(mockElController.getElevatorNum()).thenReturn(elevNum);

        when(mockElController.getFloorButtonUp(anyInt())).thenReturn(true, false, false, false);
        when(mockElController.getFloorButtonDown(anyInt())).thenReturn(false, true, true, true);

        building.init();

        ElevatorStatus[] elevators = new ElevatorStatus[elevNum];
        elevators[0] = mock(ElevatorStatus.class);
        elevators[1] = mock(ElevatorStatus.class);
        building.setElevators(elevators);
        building.setUpToDate(true);
        building.sendStatus();

        var buttonsUp = building.getButtonPressedUp();
        var buttonsDown = building.getButtonPressedDown();

        assertEquals(false, buttonsUp[0]);
        assertEquals(false, buttonsUp[1]);
        assertEquals(true, buttonsDown[0]);
        assertEquals(true, buttonsDown[1]);


        verify(mockMqttClient).publishMQTTMessage("FloorButtonUp/" + 0 + "/", Boolean.toString(buttonsUp[0]));
        verify(mockMqttClient).publishMQTTMessage("FloorButtonDown/" + 0 + "/", Boolean.toString(buttonsDown[0]));

        verify(elevators[0]).checkStatus(true);
        verify(elevators[1]).checkStatus(true);
    }

}
