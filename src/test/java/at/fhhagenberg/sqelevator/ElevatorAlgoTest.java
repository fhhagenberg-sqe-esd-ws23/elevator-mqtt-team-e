package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttClientInterface;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.MqttWireMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sqelevator.IElevator;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElevatorAlgoTest {

    @Mock
    private MqttWrapper mockMqttClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        assertNotNull(elAlgo);
        assertEquals("tcp://localhost:1883", elAlgo.getMqttConnectionString());
        assertEquals("building_controller_client", elAlgo.getClientID());

        ElevatorAlgo elAlgo1 = new ElevatorAlgo("test1", "test2");
        assertNotNull(elAlgo1);
        assertEquals("test1", elAlgo1.getMqttConnectionString());
        assertEquals("test2", elAlgo1.getClientID());
    }

    @Test
    void testMessageArrivedInit() throws Exception {
        ElevatorAlgo elMain = new ElevatorAlgo("", "");
        BuildingStorage building = new BuildingStorage(5,1);

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        MqttMessage floor = new MqttMessage("5".getBytes());
        MqttMessage elv = new MqttMessage("1".getBytes());

        assertFalse(elMain.isNumberOfFloorsInitialised());
        assertFalse(elMain.isNumberOfElevatorsInitialised());

        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "NumberFloors/", floor);
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "NumberElevators/", elv);

        elMain.setBuilding(building);

        assertTrue(elMain.isNumberOfFloorsInitialised());
        assertTrue(elMain.isNumberOfElevatorsInitialised());
    }

    @Test
    void testMessageArrivedDepth3() throws Exception {
        ElevatorAlgo elMain = new ElevatorAlgo("", "");
        BuildingStorage building = new BuildingStorage(5,1);

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        elMain.setBuilding(building);

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
        ElevatorAlgo elMain = new ElevatorAlgo("", "");
        BuildingStorage building = new BuildingStorage(5,1);

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        elMain.setBuilding(building);

        MqttMessage msg = new MqttMessage("5".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/dummy/", msg);

        msg = new MqttMessage("true".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/FloorButton/0", msg);
        assertTrue(elMain.getBuilding().getFloorButtonStatus(0)[0]);
    }

    @Test
    void testMoveElevatorUp() {
        int floors = 3;
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        BuildingStorage mockBuilding = mock(BuildingStorage.class);
        elAlgo.setBuilding(mockBuilding);
        elAlgo.setMqttWrapper(mockMqttClient);
        elAlgo.setNumberOfFloors(floors);

        boolean[] elevButtons = {false, false, true};
        when(mockBuilding.getFloorButtonStatus(0)).thenReturn(elevButtons);

        for(int i = 1; i < floors; i++){
            when(mockBuilding.getFloorState(i, true)).thenReturn(false);
        }
        when(mockBuilding.getCurrentFloor(0)).thenReturn(0, 2);

        var state = elAlgo.moveElevatorUp(0, 0);

        assertEquals(ElevatorAlgo.ElevatorState.UP, state);
        verify(mockMqttClient).publishMQTTMessage(0 + "/CommittedDirection/" , state.toString());
        verify(mockMqttClient).publishMQTTMessage(0 + "/Target/" , Integer.toString(2));
    }

    @Test
    void testMoveElevatorUpOutsidePressed() {
        int floors = 3;
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        BuildingStorage mockBuilding = mock(BuildingStorage.class);
        elAlgo.setBuilding(mockBuilding);
        elAlgo.setMqttWrapper(mockMqttClient);
        elAlgo.setNumberOfFloors(floors);

        boolean[] elevButtons = {false, false, true};
        when(mockBuilding.getFloorButtonStatus(0)).thenReturn(elevButtons);

        boolean[] floorButtons = {false, true, false};
        for(int i = 1; i < floors; i++){
            when(mockBuilding.getFloorState(i, true)).thenReturn(floorButtons[i]);
        }
        when(mockBuilding.getCurrentFloor(0)).thenReturn(0, 1);

        var state = elAlgo.moveElevatorUp(0, 0);

        assertEquals(ElevatorAlgo.ElevatorState.UP, state);
        verify(mockMqttClient).publishMQTTMessage(0 + "/CommittedDirection/" , state.toString());
        verify(mockMqttClient).publishMQTTMessage(0 + "/Target/" , Integer.toString(1));
    }

    @Test
    void testMoveElevatorUpGoDown() {
        int floors = 3;
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        BuildingStorage mockBuilding = mock(BuildingStorage.class);
        elAlgo.setBuilding(mockBuilding);
        elAlgo.setMqttWrapper(mockMqttClient);
        elAlgo.setNumberOfFloors(floors);

        boolean[] elevButtons = {true, false, false};
        when(mockBuilding.getFloorButtonStatus(0)).thenReturn(elevButtons);

        for(int i = 1; i >= 0; i--){
            when(mockBuilding.getFloorState(i, false)).thenReturn(false);
        }
        when(mockBuilding.getCurrentFloor(0)).thenReturn(2,2, 2, 0);

        var state = elAlgo.moveElevatorUp(0, 0);

        assertEquals(ElevatorAlgo.ElevatorState.DOWN, state);
        verify(mockMqttClient).publishMQTTMessage(0 + "/CommittedDirection/" , state.toString());
        verify(mockMqttClient).publishMQTTMessage(0 + "/Target/" , Integer.toString(0));
    }

    @Test
    void testMoveElevatorDown() {
        int floors = 3;
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        BuildingStorage mockBuilding = mock(BuildingStorage.class);
        elAlgo.setBuilding(mockBuilding);
        elAlgo.setMqttWrapper(mockMqttClient);
        elAlgo.setNumberOfFloors(floors);

        boolean[] elevButtons = {true, false, false};
        when(mockBuilding.getFloorButtonStatus(0)).thenReturn(elevButtons);

        boolean[] floorButtons = {false, true, false};
        for(int i = 1; i >= 0; i--){
            when(mockBuilding.getFloorState(i, false)).thenReturn(floorButtons[i]);
        }
        when(mockBuilding.getCurrentFloor(0)).thenReturn(2, 2, 1);

        var state = elAlgo.moveElevatorDown(0, 0);

        assertEquals(ElevatorAlgo.ElevatorState.DOWN, state);
        verify(mockMqttClient).publishMQTTMessage(0 + "/CommittedDirection/" , state.toString());
        verify(mockMqttClient).publishMQTTMessage(0 + "/Target/" , Integer.toString(1));
    }

    @Test
    void testWaitForDoorsOpen() {
        ElevatorAlgo elAlgo = new ElevatorAlgo("", "");
        BuildingStorage mockBuilding = mock(BuildingStorage.class);
        elAlgo.setBuilding(mockBuilding);
        when(mockBuilding.getDoorStatus(0)).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED, IElevator.ELEVATOR_DOORS_OPEN);

        elAlgo.waitForDoorsOpen(0, 1);

        verify(mockBuilding, times(2)).getDoorStatus(0);
    }


    public static class MqttTokenImpl implements IMqttToken {

        @Override
        public void waitForCompletion() {

        }

        @Override
        public void waitForCompletion(long l) {

        }

        @Override
        public boolean isComplete() {
            return false;
        }

        @Override
        public MqttException getException() {
            return null;
        }

        @Override
        public void setActionCallback(MqttActionListener mqttActionListener) {

        }

        @Override
        public MqttActionListener getActionCallback() {
            return null;
        }

        @Override
        public MqttClientInterface getClient() {
            return null;
        }

        @Override
        public String[] getTopics() {
            return new String[0];
        }

        @Override
        public void setUserContext(Object o) {

        }

        @Override
        public Object getUserContext() {
            return null;
        }

        @Override
        public int getMessageId() {
            return 0;
        }

        @Override
        public int[] getGrantedQos() {
            return new int[0];
        }

        @Override
        public int[] getReasonCodes() {
            return new int[0];
        }

        @Override
        public boolean getSessionPresent() {
            return false;
        }

        @Override
        public MqttWireMessage getResponse() {
            return null;
        }

        @Override
        public MqttProperties getResponseProperties() {
            return null;
        }

        @Override
        public MqttMessage getMessage() {
            return null;
        }

        @Override
        public MqttWireMessage getRequestMessage() {
            return null;
        }

        @Override
        public MqttProperties getRequestProperties() {
            return null;
        }
    }

    @Test
    void testCoverage() {
        ElevatorAlgo elMain = new ElevatorAlgo("", "");

        elMain.disconnected(new MqttDisconnectResponse(new MqttException(MqttException.REASON_CODE_DUPLICATE_PROPERTY)));
        elMain.mqttErrorOccurred(new MqttException(MqttException.REASON_CODE_DUPLICATE_PROPERTY));

        elMain.deliveryComplete(new MqttTokenImpl());

        elMain.connectComplete(true, "");

        elMain.authPacketArrived(1, new MqttProperties());
    }
}