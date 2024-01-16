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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MqttAdapterTest {

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testConstructor() {
        MqttAdapter mqttAd0 = new MqttAdapter("", "", "");
        assertNotNull(mqttAd0);

        assertEquals("rmi://localhost/ElevatorSim", mqttAd0.getRmiConnectionString());
        assertEquals("tcp://localhost:1883", mqttAd0.getMqttConnectionString());
        assertEquals("mqttAdapter", mqttAd0.getClientID());


        MqttAdapter mqttAd1 = new MqttAdapter("test0", "test1", "test2");
        assertNotNull(mqttAd1);

        assertEquals("test0", mqttAd1.getRmiConnectionString());
        assertEquals("test1", mqttAd1.getMqttConnectionString());
        assertEquals("test2", mqttAd1.getClientID());
    }

    @Test
    void testMessageArrivedDepth2() throws Exception {
        MqttAdapter mqttAd = new MqttAdapter("", "", "");

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        MqttMessage msg = new MqttMessage("5".getBytes());
        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI, msg);
        // return

        assertFalse(mqttAd.isInitDone());
        msg = new MqttMessage("true".getBytes());
        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI + "InitDone", msg);
        assertTrue(mqttAd.isInitDone());
    }

    @Test
    void testMessageArrivedDepth3() throws Exception {

        MqttAdapter mqttAd = new MqttAdapter("", "", "");

        BuildingStatus mbs = mock(BuildingStatus.class);

        mqttAd.setBuildingStatus(mbs);

        String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";

        MqttMessage msg = new MqttMessage("5".getBytes());
        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/", msg);

        msg = new MqttMessage("1".getBytes());

        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI + "0/CommittedDirection/", msg);
        verify(mbs).reportCommitedDirectionToRMI(0, 1);

        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI + "0/Target/", msg);
        verify(mbs).reportTargetToRMI(0, 1);

        mqttAd.messageArrived(CONTROLLER_TOPIC_RMI + "dummy/dummy/", msg);
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
        MqttAdapter maMain = new MqttAdapter("", "", "");

        maMain.disconnected(new MqttDisconnectResponse(new MqttException(MqttException.REASON_CODE_DUPLICATE_PROPERTY)));
        maMain.mqttErrorOccurred(new MqttException(MqttException.REASON_CODE_DUPLICATE_PROPERTY));

        maMain.deliveryComplete(new MqttAdapterTest.MqttTokenImpl());

        maMain.connectComplete(true, "");

        maMain.authPacketArrived(1, new MqttProperties());
    }
}
