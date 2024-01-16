package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// NOT DONE!!!!
class ElevatorMainTest {

    @Mock
    private MqttWrapper mockMqttClient;
    @Mock
    private ElevatorMain ElevatorMain;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        ElevatorMain elMain0 = new ElevatorMain("", "");
        assertNotNull(elMain0);
        assertEquals("tcp://localhost:1883", elMain0.getMqttConnectionString());
        assertEquals("building_controller_client", elMain0.getClientID());

        ElevatorMain elMain1 = new ElevatorMain("test1", "test2");
        assertNotNull(elMain1);
        assertEquals("test1", elMain1.getMqttConnectionString());
        assertEquals("test2", elMain1.getClientID());
    }

    @Test
    void testInit() {
        doNothing().when(mockMqttClient).subscribe(anyString());
        when(ElevatorMain.getMQTTClient()).thenReturn(mockMqttClient);

        ElevatorMain.init();
        verify(ElevatorMain).init();
    }

    @Test
    void testMessageArrivedInit() throws Exception {
        ElevatorMain elMain = new ElevatorMain("", "");

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        MqttMessage floor = new MqttMessage("5".getBytes());
        MqttMessage elv = new MqttMessage("1".getBytes());

        assertFalse(elMain.isNumberOfFloorsInitialised());
        assertFalse(elMain.isNumberOfElevatorsInitialised());

        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "NumberFloors/", floor);
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "NumberElevators/", elv);

        elMain.setBuilding(5, 1);

        assertTrue(elMain.isNumberOfFloorsInitialised());
        assertTrue(elMain.isNumberOfElevatorsInitialised());
    }

    @Test
    void testMessageArrivedDepth3() throws Exception {
        ElevatorMain elMain = new ElevatorMain("", "");

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        elMain.setBuilding(5, 1);

        MqttMessage msg = new MqttMessage("5".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/", msg);

        msg = new MqttMessage("true".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "FloorButtonUp/0/", msg);
        assertTrue(elMain.getBuilding().getFloorState(0, true));

        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "FloorButtonDown/0/", msg);
        assertTrue(elMain.getBuilding().getFloorState(0, false));

        msg = new MqttMessage("1".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/floorNum/", msg);
        assertEquals(1, elMain.getBuilding().getCurrentFloor(0));

        msg = new MqttMessage("1".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/position/", msg);


        msg = new MqttMessage("1".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/target/", msg);
        assertEquals(1, elMain.getBuilding().getTargetFloor(0));

        msg = new MqttMessage("1".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/committed_direction/", msg);
        assertEquals(1, elMain.getBuilding().getCommittedDirection(0));

        msg = new MqttMessage("1".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/door_status/", msg);
        assertEquals(1, elMain.getBuilding().getDoorStatus(0));
    }

    @Test
    void testMessageArrivedDepth4() throws Exception {
        ElevatorMain elMain = new ElevatorMain("", "");

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        elMain.setBuilding(5, 1);

        MqttMessage msg = new MqttMessage("5".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/dummy/", msg);

        msg = new MqttMessage("true".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/FloorButton/0", msg);
        assertTrue(elMain.getBuilding().getFloorButtonStatus(0)[0]);
    }
}