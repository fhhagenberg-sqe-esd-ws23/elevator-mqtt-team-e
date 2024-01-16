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

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// NOT DONE!!!!
class ElevatorAlgoTest {

    @Mock
    private MqttWrapper mockMqttClient;
    @Mock
    private ElevatorAlgo mockElevatorAlgo;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        ElevatorAlgo elMain0 = new ElevatorAlgo("", "");
        assertNotNull(elMain0);
        assertEquals("tcp://localhost:1883", elMain0.getMqttConnectionString());
        assertEquals("building_controller_client", elMain0.getClientID());

        ElevatorAlgo elMain1 = new ElevatorAlgo("test1", "test2");
        assertNotNull(elMain1);
        assertEquals("test1", elMain1.getMqttConnectionString());
        assertEquals("test2", elMain1.getClientID());
    }

    @Test
    void testInit() {

        doNothing().when(mockMqttClient).subscribe(anyString());

        when(mockElevatorAlgo.getMQTTClient()).thenReturn(mockMqttClient);

        mockElevatorAlgo.init();
        verify(mockElevatorAlgo).init();

    }

    @Test
    void testMessageArrivedInit() throws Exception {
        ElevatorAlgo elMain = new ElevatorAlgo("", "");

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
        ElevatorAlgo elMain = new ElevatorAlgo("", "");

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
        ElevatorAlgo elMain = new ElevatorAlgo("", "");

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        elMain.setBuilding(5, 1);

        MqttMessage msg = new MqttMessage("5".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/dummy/", msg);

        msg = new MqttMessage("true".getBytes());
        elMain.messageArrived(CONTROLLER_TOPIC_RMI + "0/FloorButton/0", msg);
        assertTrue(elMain.getBuilding().getFloorButtonStatus(0)[0]);
    }

    public class MqttTokenImpl implements IMqttToken {

        @Override
        public void waitForCompletion() throws MqttException {

        }

        @Override
        public void waitForCompletion(long l) throws MqttException {

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
        public MqttMessage getMessage() throws MqttException {
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